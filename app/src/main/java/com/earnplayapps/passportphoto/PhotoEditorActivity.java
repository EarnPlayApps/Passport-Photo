package com.earnplayapps.passportphoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.*;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

/** Passport Photo editor: local editing, requirement-aware validation and export. */
public class PhotoEditorActivity extends Activity {
    Bitmap original, working;
    ImageView preview;
    Requirement req;
    String reqId;
    final Deque<Bitmap> undo = new ArrayDeque<>();
    final Deque<Bitmap> redo = new ArrayDeque<>();
    boolean showingOriginal = false;

    int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setMinHeight(dp(48));return b;}
    TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setPadding(dp(4),dp(4),dp(4),dp(4));return t;}

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        reqId=getIntent().getStringExtra("requirement_id");
        try{Uri u=getIntent().getData();if(u!=null)original=decodeUri(u);}catch(Exception ignored){}
        if(original==null){Toast.makeText(this,"Tidak dapat membaca foto",Toast.LENGTH_LONG).show();finish();return;}
        working=original.copy(Bitmap.Config.ARGB_8888,false);
        for(Requirement r:MalaysiaRequirements.all())if(r.id.equals(reqId))req=r;
        if(req==null&&getIntent().hasExtra("custom_width_mm")){
            int w=getIntent().getIntExtra("custom_width_mm",0),h=getIntent().getIntExtra("custom_height_mm",0);
            String bg=getIntent().getStringExtra("custom_background");
            req=new Requirement("CUSTOM-"+System.currentTimeMillis(),"CUSTOM","User defined","Custom photo","User","custom",1,w,h,w+" × "+h+" mm",bg==null?"Tidak dinyatakan":bg,"User-defined; not an official standard","JPG","Tidak dinyatakan","CUSTOM","CUSTOM","");
        }
        build();
    }

    Bitmap decodeUri(Uri uri)throws Exception{
        BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;
        InputStream a=getContentResolver().openInputStream(uri);if(a==null)throw new IOException("Input unavailable");
        BitmapFactory.decodeStream(a,null,o);a.close();int max=2400,s=1;
        while(Math.max(o.outWidth,o.outHeight)/(s*2)>max)s*=2;
        o.inJustDecodeBounds=false;o.inSampleSize=s;o.inPreferredConfig=Bitmap.Config.ARGB_8888;
        InputStream in=getContentResolver().openInputStream(uri);if(in==null)throw new IOException("Input unavailable");
        Bitmap b=BitmapFactory.decodeStream(in,null,o);in.close();return b;
    }

    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(12),dp(10),dp(12),dp(12));root.setBackgroundColor(0xfff5f7fb);
        TextView title=text("Passport Photo",24,0xff17233d);title.setGravity(Gravity.CENTER_VERTICAL);root.addView(title,new LinearLayout.LayoutParams(-1,dp(48)));
        TextView sub=text(req==null?"Photo Editor":req.purpose+"\n"+req.size+"  •  "+req.background+"\nEvidence: "+req.evidence,14,0xff5b6475);root.addView(sub);
        preview=new ImageView(this);preview.setScaleType(ImageView.ScaleType.FIT_CENTER);preview.setBackgroundColor(0xffe4e8ef);preview.setImageBitmap(working);root.addView(preview,new LinearLayout.LayoutParams(-1,dp(390)));

        LinearLayout r1=row();add(r1,"Auto Align",v->transform(1));add(r1,"Crop",v->transform(2));add(r1,"Rotate",v->transform(3));add(r1,"Flip",v->transform(4));root.addView(r1);
        LinearLayout r2=row();add(r2,"Background",v->backgroundDialog());add(r2,"Adjust",v->adjustDialog());add(r2,"Undo",v->undo());add(r2,"Redo",v->redo());root.addView(r2);
        LinearLayout r3=row();add(r3,"Before / After",v->{showingOriginal=!showingOriginal;preview.setImageBitmap(showingOriginal?original:working);});add(r3,"Reset",v->reset());add(r3,"Check Photo",v->check());root.addView(r3);
        Button print=btn("PRINT STUDIO");print.setOnClickListener(v->checkAndPrint());root.addView(print);
        LinearLayout r4=row();Button share=btn("Share");share.setOnClickListener(v->checkAndShare());r4.addView(share,new LinearLayout.LayoutParams(0,dp(54),1));Button ex=btn("FINAL VALIDATION & EXPORT");ex.setOnClickListener(v->check());r4.addView(ex,new LinearLayout.LayoutParams(0,dp(54),2));root.addView(r4);
        setContentView(root);
    }
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setPadding(0,dp(2),0,dp(2));return l;}
    void add(LinearLayout l,String s,android.view.View.OnClickListener c){Button b=btn(s);b.setOnClickListener(c);l.addView(b,new LinearLayout.LayoutParams(0,dp(50),1));}
    void pushUndo(){if(undo.size()>=8)undo.removeLast();undo.push(working);while(!redo.isEmpty())redo.pop();}
    void transform(int type){
        if(type<=2&& (req==null||req.widthMm<=0||req.heightMm<=0)){Toast.makeText(this,"Pilih ukuran rasmi atau Custom Size dahulu.",Toast.LENGTH_SHORT).show();return;}
        pushUndo();
        if(type==1)working=PhotoEngine.autoCrop(working,req.widthMm,req.heightMm);
        else if(type==2)working=PhotoEngine.cropRatio(working,req.widthMm,req.heightMm);
        else if(type==3)working=PhotoEngine.rotate(working,90);
        else working=PhotoEngine.flipHorizontal(working);
        showWorking();
    }
    void undo(){if(!undo.isEmpty()){redo.push(working);working=undo.pop();showWorking();}else Toast.makeText(this,"Tiada perubahan untuk di-undo.",Toast.LENGTH_SHORT).show();}
    void redo(){if(!redo.isEmpty()){undo.push(working);working=redo.pop();showWorking();}else Toast.makeText(this,"Tiada perubahan untuk di-redo.",Toast.LENGTH_SHORT).show();}
    void reset(){while(!undo.isEmpty())undo.pop();while(!redo.isEmpty())redo.pop();working=original.copy(Bitmap.Config.ARGB_8888,false);showWorking();Toast.makeText(this,"Foto dikembalikan ke asal.",Toast.LENGTH_SHORT).show();}
    void showWorking(){showingOriginal=false;preview.setImageBitmap(working);}

    boolean allowedBackground(int index){
        if(req==null||req.background==null)return index==0;
        String rule=req.background.toLowerCase(Locale.US);
        if(rule.contains("tidak dinyatakan")||rule.contains("unspecified")||rule.contains("pilihan pengguna")||rule.contains("custom"))return true;
        if(index==1)return rule.contains("white")||rule.contains("putih")||rule.contains("white/light");
        if(index==2)return rule.contains("blue")||rule.contains("biru");
        if(index==3)return rule.contains("grey")||rule.contains("gray")||rule.contains("kelabu");
        return false;
    }
    void backgroundDialog(){
        String[] opts={"Keep original","White","Blue","Light grey"};
        new AlertDialog.Builder(this).setTitle("Background").setItems(opts,(d,w)->{
            if(w==0)return;
            if(!allowedBackground(w)){Toast.makeText(this,"Pilihan background tidak dibenarkan oleh requirement terpilih.",Toast.LENGTH_LONG).show();return;}
            pushUndo();int c=w==1?Color.WHITE:w==2?Color.rgb(30,95,190):Color.rgb(235,235,235);working=replaceBackground(working,c);showWorking();
        }).show();
    }
    Bitmap replaceBackground(Bitmap src,int color){
        Bitmap out=src.copy(Bitmap.Config.ARGB_8888,true);int w=src.getWidth(),h=src.getHeight();boolean[] seen=new boolean[w*h];ArrayDeque<Integer> q=new ArrayDeque<>();
        int seed=src.getPixel(0,0),sr=Color.red(seed),sg=Color.green(seed),sb=Color.blue(seed),tol=55;
        for(int x=0;x<w;x++){q.add(x);q.add((h-1)*w+x);}for(int y=0;y<h;y++){q.add(y*w);q.add(y*w+w-1);}
        while(!q.isEmpty()){int idx=q.removeFirst();if(idx<0||idx>=seen.length||seen[idx])continue;seen[idx]=true;int x=idx%w,y=idx/w,p=src.getPixel(x,y);int d=Math.abs(Color.red(p)-sr)+Math.abs(Color.green(p)-sg)+Math.abs(Color.blue(p)-sb);if(d>tol)continue;out.setPixel(x,y,color);if(x>0)q.add(idx-1);if(x<w-1)q.add(idx+1);if(y>0)q.add(idx-w);if(y<h-1)q.add(idx+w);}
        return out;
    }
    void adjustDialog(){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);SeekBar br=new SeekBar(this);br.setMax(100);br.setProgress(50);SeekBar co=new SeekBar(this);co.setMax(100);co.setProgress(50);l.addView(text("Brightness",14,0xff3b4659));l.addView(br);l.addView(text("Contrast",14,0xff3b4659));l.addView(co);
        new AlertDialog.Builder(this).setTitle("Adjust").setView(l).setPositiveButton("Apply",(d,w)->{pushUndo();working=PhotoEngine.adjust(working,br.getProgress()-50,co.getProgress()-50);showWorking();}).setNegativeButton("Cancel",null).show();
    }

    void check(){checkInternal(false);}
    void checkAndShare(){checkInternal(true);}
    void checkAndPrint(){
        ComplianceEngine.Report r=ComplianceEngine.check(working,req);
        if(!r.pass){showReport(r,false);return;}
        showReport(r,true);
    }
    void showReport(ComplianceEngine.Report r,boolean printAfterPass){
        StringBuilder s=new StringBuilder(r.summary+"\n\n");
        for(ComplianceEngine.Issue i:r.issues)s.append(i.status).append(" • ").append(i.title).append("\n").append(i.detail).append("\n\n");
        String positive=printAfterPass?"OPEN PRINT STUDIO":(r.pass?"EXPORT":"OK");
        new AlertDialog.Builder(this).setTitle("Compliance Checker").setMessage(s.toString()).setPositiveButton(positive,(d,w)->{if(r.pass){if(printAfterPass)preparePrint();else export(false);}}).setNegativeButton("Close",null).show();
    }
    void checkInternal(boolean shareAfterPass){
        ComplianceEngine.Report r=ComplianceEngine.check(working,req);showReport(r,shareAfterPass);
        if(shareAfterPass&&r.pass){
            // The dialog action above handles export/share; keep this branch only as a semantic guard.
        }
    }
    long parseLimitBytes(String rule){try{String x=rule.toLowerCase().trim().replace(',','.');String num=x.replaceAll("[^0-9.]","");if(num.isEmpty())return-1;double n=Double.parseDouble(num);if(x.contains("mb"))return(long)(n*1024d*1024d);if(x.contains("kb"))return(long)(n*1024d);}catch(Exception ignored){}return-1;}

    Uri writeExport()throws Exception{
        Bitmap finalBitmap=working;if(req!=null&&req.widthMm>0&&req.heightMm>0)finalBitmap=PhotoEngine.resizeForPrintMm(working,req.widthMm,req.heightMm);
        String rule=req==null?"":req.maxFileSize==null?"":req.maxFileSize;long limit=parseLimitBytes(rule);
        boolean png=req!=null&&req.format!=null&&req.format.toLowerCase(Locale.US).contains("png")&&!req.format.toLowerCase(Locale.US).contains("jpg");byte[] data;
        if(png)data=PhotoEngine.png(finalBitmap);
        else if(rule.toLowerCase(Locale.US).contains("minimum")){data=PhotoEngine.jpeg(finalBitmap,100);if(limit>0&&data.length<limit)throw new IOException("Final file is below the official minimum size.");}
        else if(limit>0){data=null;for(int q=95;q>=30;q-=5){byte[] test=PhotoEngine.jpeg(finalBitmap,q);if(test.length<=limit){data=test;break;}}if(data==null)throw new IOException("Unable to meet the official file-size limit without excessive compression.");}
        else data=PhotoEngine.jpeg(finalBitmap,95);
        String ext=png?"png":"jpg",name="PassportPhoto_"+System.currentTimeMillis()+"."+ext;ContentValues v=new ContentValues();v.put(MediaStore.Images.Media.DISPLAY_NAME,name);v.put(MediaStore.Images.Media.MIME_TYPE,png?"image/png":"image/jpeg");if(android.os.Build.VERSION.SDK_INT>=29)v.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Passport Photo");
        Uri u=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,v);if(u==null)throw new IOException("MediaStore insert failed");OutputStream o=getContentResolver().openOutputStream(u);if(o==null)throw new IOException("Output stream failed");o.write(data);o.close();ProjectStore.add(this,name);return u;
    }
    void export(boolean share){try{Uri u=writeExport();if(share){Intent i=new Intent(Intent.ACTION_SEND);i.setType(getContentResolver().getType(u));i.putExtra(Intent.EXTRA_STREAM,u);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(i,"Share Passport Photo"));}else{Toast.makeText(this,"Export berjaya. Disimpan dalam Pictures/Passport Photo.",Toast.LENGTH_LONG).show();}}catch(Exception e){Toast.makeText(this,"Export gagal: "+e.getMessage(),Toast.LENGTH_LONG).show();}}

    void preparePrint(){try{File f=new File(getCacheDir(),"print_source.jpg");FileOutputStream o=new FileOutputStream(f);working.compress(Bitmap.CompressFormat.JPEG,95,o);o.close();Intent i=new Intent(this,PrintStudioActivity.class);i.putExtra("bitmap_path",f.getAbsolutePath());if(req!=null){i.putExtra("width_mm",req.widthMm);i.putExtra("height_mm",req.heightMm);}startActivity(i);}catch(Exception e){Toast.makeText(this,"Print preparation failed",Toast.LENGTH_LONG).show();}}
}
