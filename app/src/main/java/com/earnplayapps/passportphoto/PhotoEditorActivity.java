package com.earnplayapps.passportphoto;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class PhotoEditorActivity extends Activity {
    Bitmap original,working; ImageView preview; Requirement req; String reqId; final Deque<Bitmap> undo=new ArrayDeque<>(); boolean showingOriginal=false;
    int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setMinHeight(dp(48));return b;}

    @Override public void onCreate(Bundle b){super.onCreate(b);reqId=getIntent().getStringExtra("requirement_id");
        try{Uri u=getIntent().getData();if(u!=null)original=decodeUri(u);}catch(Exception ignored){}
        if(original==null){Toast.makeText(this,"Tidak dapat membaca foto",Toast.LENGTH_LONG).show();finish();return;}
        working=original;for(Requirement r:MalaysiaRequirements.all())if(r.id.equals(reqId))req=r;
        if(req==null&&getIntent().hasExtra("custom_width_mm")){
            int w=getIntent().getIntExtra("custom_width_mm",0),h=getIntent().getIntExtra("custom_height_mm",0);String bg=getIntent().getStringExtra("custom_background");
            req=new Requirement("CUSTOM-"+System.currentTimeMillis(),"CUSTOM","User defined", "Custom photo", "User","custom",1,w,h,w+" × "+h+" mm",bg==null?"Tidak dinyatakan":bg,"User-defined; not an official standard","JPG","Tidak dinyatakan","CUSTOM","CUSTOM","");
        }
        build();
    }
    Bitmap decodeUri(Uri uri)throws Exception{
        BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;InputStream a=getContentResolver().openInputStream(uri);if(a==null)throw new IOException("Input unavailable");BitmapFactory.decodeStream(a,null,o);a.close();
        int max=2400,s=1;while(Math.max(o.outWidth,o.outHeight)/(s*2)>max)s*=2;o.inJustDecodeBounds=false;o.inSampleSize=s;o.inPreferredConfig=Bitmap.Config.ARGB_8888;InputStream in=getContentResolver().openInputStream(uri);if(in==null)throw new IOException("Input unavailable");Bitmap b=BitmapFactory.decodeStream(in,null,o);in.close();return b;
    }
    void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(12),dp(12),dp(12),dp(12));root.setBackgroundColor(0xfff5f7fb);
        TextView t=new TextView(this);t.setText("Passport Photo • Editor");t.setTextSize(24);t.setTextColor(0xff17233d);root.addView(t);
        TextView info=new TextView(this);info.setText(req==null?"Photo Builder":req.purpose+"\n"+req.size+" • "+req.background+"\nEvidence: "+req.evidence);info.setTextColor(0xff5b6475);root.addView(info);
        preview=new ImageView(this);preview.setScaleType(ImageView.ScaleType.FIT_CENTER);preview.setBackgroundColor(0xffe4e8ef);preview.setImageBitmap(working);root.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout r1=new LinearLayout(this);r1.setOrientation(LinearLayout.HORIZONTAL);
        Button auto=btn("Auto Align");auto.setOnClickListener(v->{if(req!=null&&req.widthMm>0){pushUndo();working=PhotoEngine.autoCrop(working,req.widthMm,req.heightMm);showWorking();}else Toast.makeText(this,"Pilih ukuran rasmi atau Custom Size dahulu.",Toast.LENGTH_SHORT).show();});r1.addView(auto,new LinearLayout.LayoutParams(0,dp(48),1));
        Button crop=btn("Crop");crop.setOnClickListener(v->{if(req!=null&&req.widthMm>0){pushUndo();working=PhotoEngine.cropRatio(working,req.widthMm,req.heightMm);showWorking();}else Toast.makeText(this,"Tiada ukuran untuk crop.",Toast.LENGTH_SHORT).show();});r1.addView(crop,new LinearLayout.LayoutParams(0,dp(48),1));
        Button rot=btn("Rotate");rot.setOnClickListener(v->{pushUndo();working=PhotoEngine.rotate(working,90);showWorking();});r1.addView(rot,new LinearLayout.LayoutParams(0,dp(48),1));root.addView(r1);
        LinearLayout r2=new LinearLayout(this);r2.setOrientation(LinearLayout.HORIZONTAL);
        Button bg=btn("Background");bg.setOnClickListener(v->backgroundDialog());r2.addView(bg,new LinearLayout.LayoutParams(0,dp(48),1));
        Button adj=btn("Adjust");adj.setOnClickListener(v->adjustDialog());r2.addView(adj,new LinearLayout.LayoutParams(0,dp(48),1));
        Button undoBtn=btn("Undo");undoBtn.setOnClickListener(v->{if(!undo.isEmpty()){working=undo.pop();showWorking();}else Toast.makeText(this,"Tiada perubahan untuk di-undo.",Toast.LENGTH_SHORT).show();});r2.addView(undoBtn,new LinearLayout.LayoutParams(0,dp(48),1));root.addView(r2);
        LinearLayout r3=new LinearLayout(this);r3.setOrientation(LinearLayout.HORIZONTAL);
        Button compare=btn("Before / After");compare.setOnClickListener(v->{showingOriginal=!showingOriginal;preview.setImageBitmap(showingOriginal?original:working);});r3.addView(compare,new LinearLayout.LayoutParams(0,dp(48),1));
        Button check=btn("Check Photo");check.setOnClickListener(v->check());r3.addView(check,new LinearLayout.LayoutParams(0,dp(48),1));root.addView(r3);
        Button print=btn("Print Studio");print.setOnClickListener(v->preparePrint());root.addView(print);
        Button ex=btn("FINAL VALIDATION & EXPORT");ex.setOnClickListener(v->check());root.addView(ex,new LinearLayout.LayoutParams(-1,dp(54)));setContentView(root);
    }
    void pushUndo(){if(undo.size()>=8)undo.removeLast();undo.push(working);}
    void showWorking(){showingOriginal=false;preview.setImageBitmap(working);}
    void backgroundDialog(){String[] opts={"Keep original","White","Blue","Light grey"};new AlertDialog.Builder(this).setTitle("Background").setItems(opts,(d,w)->{if(w==0)return;if(req!=null&&!req.background.toLowerCase().contains("tidak dinyatakan")&&!req.background.toLowerCase().contains("unspecified")&&!req.background.toLowerCase().contains("white")&&!req.background.toLowerCase().contains("blue")&&!req.background.toLowerCase().contains("grey")){Toast.makeText(this,"Pilihan background mesti ikut requirement rasmi.",Toast.LENGTH_LONG).show();return;}pushUndo();int c=w==1?Color.WHITE:w==2?Color.rgb(30,95,190):Color.rgb(235,235,235);working=replaceBackground(working,c);showWorking();}).show();}
    Bitmap replaceBackground(Bitmap src,int color){
        Bitmap out=src.copy(Bitmap.Config.ARGB_8888,true);int w=src.getWidth(),h=src.getHeight();boolean[] seen=new boolean[w*h];java.util.ArrayDeque<Integer> q=new java.util.ArrayDeque<>();
        int seed=src.getPixel(0,0),sr=Color.red(seed),sg=Color.green(seed),sb=Color.blue(seed),tol=55;
        for(int x=0;x<w;x++){q.add(x);q.add((h-1)*w+x);}for(int y=0;y<h;y++){q.add(y*w);q.add(y*w+w-1);}
        while(!q.isEmpty()){int idx=q.removeFirst();if(idx<0||idx>=seen.length||seen[idx])continue;seen[idx]=true;int x=idx%w,y=idx/w,p=src.getPixel(x,y);int d=Math.abs(Color.red(p)-sr)+Math.abs(Color.green(p)-sg)+Math.abs(Color.blue(p)-sb);if(d>tol)continue;out.setPixel(x,y,color);if(x>0)q.add(idx-1);if(x<w-1)q.add(idx+1);if(y>0)q.add(idx-w);if(y<h-1)q.add(idx+w);}
        return out;
    }
    void adjustDialog(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);SeekBar br=new SeekBar(this);br.setMax(100);br.setProgress(50);SeekBar co=new SeekBar(this);co.setMax(100);co.setProgress(50);l.addView(label("Brightness"));l.addView(br);l.addView(label("Contrast"));l.addView(co);new AlertDialog.Builder(this).setTitle("Adjust").setView(l).setPositiveButton("Apply",(d,w)->{pushUndo();working=PhotoEngine.adjust(working,br.getProgress()-50,co.getProgress()-50);showWorking();}).setNegativeButton("Cancel",null).show();}
    TextView label(String s){TextView t=new TextView(this);t.setText(s);return t;}
    void check(){ComplianceEngine.Report r=ComplianceEngine.check(working,req);StringBuilder s=new StringBuilder(r.summary+"\n\n");for(ComplianceEngine.Issue i:r.issues)s.append(i.status).append(" • ").append(i.title).append("\n").append(i.detail).append("\n\n");new AlertDialog.Builder(this).setTitle("Compliance Checker").setMessage(s.toString()).setPositiveButton(r.pass?"EXPORT":"OK",(d,w)->{if(r.pass)export();}).setNegativeButton("Close",null).show();}
    long parseLimitBytes(String rule){try{String x=rule.toLowerCase().trim().replace(',','.');String num=x.replaceAll("[^0-9.]","");if(num.isEmpty())return-1;double n=Double.parseDouble(num);if(x.contains("mb"))return(long)(n*1024d*1024d);if(x.contains("kb"))return(long)(n*1024d);}catch(Exception ignored){}return-1;}
    void export(){try{Bitmap finalBitmap=working;if(req!=null&&req.widthMm>0&&req.heightMm>0)finalBitmap=PhotoEngine.resizeForPrintMm(working,req.widthMm,req.heightMm);String rule=req==null?"":req.maxFileSize;long limit=parseLimitBytes(rule);boolean png=req!=null&&req.format!=null&&req.format.toLowerCase().contains("png")&&!req.format.toLowerCase().contains("jpg");byte[] data;
        if(png)data=PhotoEngine.png(finalBitmap);else if(rule.toLowerCase().contains("minimum")){data=PhotoEngine.jpeg(finalBitmap,100);if(limit>0&&data.length<limit){Toast.makeText(this,"Fail: final file is below the official minimum size.",Toast.LENGTH_LONG).show();return;}}
        else if(limit>0){data=null;for(int q=95;q>=30;q-=5){byte[] test=PhotoEngine.jpeg(finalBitmap,q);if(test.length<=limit){data=test;break;}}if(data==null){Toast.makeText(this,"Fail: unable to meet the official file-size limit without excessive compression.",Toast.LENGTH_LONG).show();return;}}
        else data=PhotoEngine.jpeg(finalBitmap,95);
        String ext=png?"png":"jpg",name="PassportPhoto_"+System.currentTimeMillis()+"."+ext;ContentValues v=new ContentValues();v.put(MediaStore.Images.Media.DISPLAY_NAME,name);v.put(MediaStore.Images.Media.MIME_TYPE,png?"image/png":"image/jpeg");if(android.os.Build.VERSION.SDK_INT>=29)v.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Passport Photo");Uri u=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,v);if(u==null)throw new IOException("MediaStore insert failed");OutputStream o=getContentResolver().openOutputStream(u);if(o==null)throw new IOException("Output stream failed");o.write(data);o.close();ProjectStore.add(this,name);Toast.makeText(this,"Export berjaya: "+name+" ("+finalBitmap.getWidth()+"×"+finalBitmap.getHeight()+" px)",Toast.LENGTH_LONG).show();
    }catch(Exception e){Toast.makeText(this,"Export gagal: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
    void preparePrint(){try{File f=new File(getCacheDir(),"print_source.jpg");FileOutputStream o=new FileOutputStream(f);working.compress(Bitmap.CompressFormat.JPEG,95,o);o.close();Intent i=new Intent(this,PrintStudioActivity.class);i.putExtra("bitmap_path",f.getAbsolutePath());if(req!=null){i.putExtra("width_mm",req.widthMm);i.putExtra("height_mm",req.heightMm);}startActivity(i);}catch(Exception e){Toast.makeText(this,"Print preparation failed",Toast.LENGTH_LONG).show();}}
}
