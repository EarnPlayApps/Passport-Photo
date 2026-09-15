package com.earnplayapps.passportphoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.io.*;

/** Print layout builder. Keeps documented physical photo dimensions intact when a requirement supplies them. */
public class PrintStudioActivity extends Activity {
    Bitmap source, sheet;
    ImageView preview;
    int count = 6;
    String paper = "A4";
    int widthMm = 0, heightMm = 0;
    int gapMm = 2;
    boolean cutGuides = true, exactFit = true;
    int customPaperW = 210, customPaperH = 297;
    String lastSavedName = null;

    int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }
    Button btn(String s) { Button b = new Button(this); b.setText(s); b.setAllCaps(false); b.setMinHeight(dp(46)); return b; }
    TextView text(String s, float size) { TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.rgb(30,42,68)); t.setPadding(0,dp(4),0,dp(4)); return t; }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        String path = getIntent().getStringExtra("bitmap_path");
        source = BitmapFactory.decodeFile(path);
        widthMm = getIntent().getIntExtra("width_mm", 0);
        heightMm = getIntent().getIntExtra("height_mm", 0);
        if (source == null) { finish(); return; }
        build();
    }

    void build() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16),dp(14),dp(16),dp(18));
        root.setBackgroundColor(Color.rgb(245,247,251));

        TextView title = text("Print Studio", 25); title.setTypeface(null,1); root.addView(title);
        String sizeText = widthMm > 0 && heightMm > 0 ? widthMm+" × "+heightMm+" mm" : "Custom / source proportion";
        root.addView(text("Passport Photo • "+sizeText+" • 300 DPI", 14));

        LinearLayout paperRow = new LinearLayout(this); paperRow.setOrientation(LinearLayout.HORIZONTAL);
        Button a4 = btn("A4\n210 × 297 mm"), p46 = btn("4×6\n102 × 152 mm"), custom = btn("Custom paper");
        a4.setOnClickListener(v->{paper="A4";render();});
        p46.setOnClickListener(v->{paper="4x6";render();});
        custom.setOnClickListener(v->customPaperDialog());
        paperRow.addView(a4,new LinearLayout.LayoutParams(0,dp(60),1));
        paperRow.addView(p46,new LinearLayout.LayoutParams(0,dp(60),1));
        paperRow.addView(custom,new LinearLayout.LayoutParams(0,dp(60),1));
        root.addView(paperRow);

        preview = new ImageView(this);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        preview.setBackgroundColor(Color.WHITE);
        root.addView(preview,new LinearLayout.LayoutParams(-1,dp(390)));

        root.addView(text("Copies",16));
        LinearLayout copies = new LinearLayout(this); copies.setGravity(Gravity.CENTER_VERTICAL);
        for (int n : new int[]{2,4,6,8,10,12}) {
            Button q=btn(String.valueOf(n));
            q.setOnClickListener(v->{count=Integer.parseInt(((Button)v).getText().toString());render();});
            copies.addView(q,new LinearLayout.LayoutParams(0,dp(48),1));
        }
        Button customCount=btn("Custom"); customCount.setOnClickListener(v->customCountDialog());
        copies.addView(customCount,new LinearLayout.LayoutParams(0,dp(48),1));
        root.addView(copies);

        LinearLayout options = new LinearLayout(this); options.setOrientation(LinearLayout.HORIZONTAL);
        Button spacing=btn("Spacing: "+gapMm+" mm");
        spacing.setOnClickListener(v->spacingDialog(spacing));
        Button guides=btn(cutGuides?"Cut guides: ON":"Cut guides: OFF");
        guides.setOnClickListener(v->{cutGuides=!cutGuides;guides.setText(cutGuides?"Cut guides: ON":"Cut guides: OFF");render();});
        options.addView(spacing,new LinearLayout.LayoutParams(0,dp(52),1));
        options.addView(guides,new LinearLayout.LayoutParams(0,dp(52),1));
        root.addView(options);

        TextView status=text("",14); status.setGravity(Gravity.CENTER); root.addView(status);
        Button save=btn("SAVE PRINT SHEET");
        save.setOnClickListener(v->save()); root.addView(save,new LinearLayout.LayoutParams(-1,dp(54)));
        Button share=btn("SHARE PRINT SHEET");
        share.setOnClickListener(v->share()); root.addView(share,new LinearLayout.LayoutParams(-1,dp(52)));
        Button printInfo=btn("PRINT / SEND TO PRINTER");
        printInfo.setOnClickListener(v->printSheet()); root.addView(printInfo,new LinearLayout.LayoutParams(-1,dp(52)));
        Button back=btn("Back"); back.setOnClickListener(v->finish()); root.addView(back);

        scroll.addView(root); setContentView(scroll); render();
    }

    int px(float mm) { return Math.max(1,Math.round(mm*300f/25.4f)); }
    int paperW() { return paper.equals("4x6") ? 102 : paper.equals("CUSTOM") ? customPaperW : 210; }
    int paperH() { return paper.equals("4x6") ? 152 : paper.equals("CUSTOM") ? customPaperH : 297; }

    void render() {
        int w=px(paperW()), h=px(paperH());
        sheet=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas c=new Canvas(sheet); c.drawColor(Color.WHITE);
        int photoW=widthMm>0?px(widthMm):0, photoH=heightMm>0?px(heightMm):0;
        int gap=px(gapMm), margin=px(3);
        int cols,rows;
        if(photoW>0&&photoH>0){
            cols=Math.max(1,(w-2*margin+gap)/(photoW+gap));
            rows=Math.max(1,(h-2*margin+gap)/(photoH+gap));
            exactFit=cols*rows>=count;
            int usedCols=Math.min(cols,Math.max(1,count));
            rows=(count+usedCols-1)/usedCols;
            float totalW=usedCols*photoW+(usedCols-1)*gap;
            float totalH=rows*photoH+(rows-1)*gap;
            cols=usedCols;
            for(int n=0;n<count;n++){
                int col=n%cols,row=n/cols;
                float x=(w-totalW)/2f+col*(photoW+gap);
                float y=(h-totalH)/2f+row*(photoH+gap);
                if(exactFit) c.drawBitmap(Bitmap.createScaledBitmap(source,photoW,photoH,true),x,y,null);
                drawGuide(c,x,y,photoW,photoH);
            }
        } else {
            cols=Math.min(4,Math.max(1,count)); rows=(count+cols-1)/cols; exactFit=true;
            float cellW=(w-gap*(cols+1))/(float)cols,cellH=(h-gap*(rows+1))/(float)rows;
            for(int n=0;n<count;n++){
                int col=n%cols,row=n/cols;
                float scale=Math.min((cellW-2*margin)/source.getWidth(),(cellH-2*margin)/source.getHeight());
                int pw=Math.max(1,Math.round(source.getWidth()*scale)),ph=Math.max(1,Math.round(source.getHeight()*scale));
                float x=gap+col*(cellW+gap)+(cellW-pw)/2f,y=gap+row*(cellH+gap)+(cellH-ph)/2f;
                c.drawBitmap(Bitmap.createScaledBitmap(source,pw,ph,true),x,y,null); drawGuide(c,x,y,pw,ph);
            }
        }
        preview.setImageBitmap(sheet);
        TextView status=findStatus();
        if(status!=null) status.setText(exactFit ? "READY • "+count+" copies • "+paperW()+" × "+paperH()+" mm paper" : "NOT FIT • reduce copies, spacing, or choose a larger paper");
    }

    TextView findStatus(){
        View v=((ScrollView)preview.getParent().getParent()).getChildAt(0);
        if(!(v instanceof LinearLayout))return null;
        LinearLayout l=(LinearLayout)v;
        for(int i=0;i<l.getChildCount();i++){View x=l.getChildAt(i);if(x instanceof TextView && ((TextView)x).getText().toString().startsWith("READY")||x instanceof TextView && ((TextView)x).getText().toString().startsWith("NOT FIT"))return (TextView)x;}
        return null;
    }

    void drawGuide(Canvas c,float x,float y,float w,float h){
        if(!cutGuides)return; Paint p=new Paint();p.setColor(Color.rgb(190,196,205));p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);c.drawRect(x,y,x+w,y+h,p);
    }

    void customPaperDialog(){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(8),0,dp(8),0);
        EditText w=new EditText(this);w.setHint("Width mm (e.g. 210)");w.setInputType(2|8192);
        EditText h=new EditText(this);h.setHint("Height mm (e.g. 297)");h.setInputType(2|8192);l.addView(w);l.addView(h);
        new AlertDialog.Builder(this).setTitle("Custom paper size").setView(l).setPositiveButton("Apply",(d,x)->{try{customPaperW=Math.max(30,Integer.parseInt(w.getText().toString()));customPaperH=Math.max(30,Integer.parseInt(h.getText().toString()));paper="CUSTOM";render();}catch(Exception e){Toast.makeText(this,"Masukkan saiz mm yang sah.",Toast.LENGTH_SHORT).show();}}).setNegativeButton("Cancel",null).show();
    }
    void customCountDialog(){
        EditText e=new EditText(this);e.setHint("Copies (1–100)");e.setInputType(2|8192);
        new AlertDialog.Builder(this).setTitle("Custom quantity").setView(e).setPositiveButton("Apply",(d,x)->{try{count=Math.max(1,Math.min(100,Integer.parseInt(e.getText().toString())));render();}catch(Exception z){Toast.makeText(this,"Masukkan kuantiti yang sah.",Toast.LENGTH_SHORT).show();}}).setNegativeButton("Cancel",null).show();
    }
    void spacingDialog(Button b){
        EditText e=new EditText(this);e.setHint("Spacing in mm (0–20)");e.setInputType(2|8192);e.setText(String.valueOf(gapMm));
        new AlertDialog.Builder(this).setTitle("Photo spacing").setView(e).setPositiveButton("Apply",(d,x)->{try{gapMm=Math.max(0,Math.min(20,Integer.parseInt(e.getText().toString())));b.setText("Spacing: "+gapMm+" mm");render();}catch(Exception z){}}).setNegativeButton("Cancel",null).show();
    }

    void save(){
        if(widthMm>0&&heightMm>0&&!exactFit){new AlertDialog.Builder(this).setTitle("Layout cannot fit").setMessage("The selected quantity cannot fit while preserving the documented "+widthMm+" × "+heightMm+" mm photo size. Reduce quantity/spacing or choose a larger paper.").setPositiveButton("OK",null).show();return;}
        try{
            String safePaper=paper.equals("CUSTOM")?customPaperW+"x"+customPaperH:paper;
            lastSavedName="PassportPhoto_Print_"+safePaper+"_"+count+"_"+System.currentTimeMillis()+".jpg";
            ContentValues v=new ContentValues();v.put(MediaStore.Images.Media.DISPLAY_NAME,lastSavedName);v.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
            if(android.os.Build.VERSION.SDK_INT>=29)v.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Passport Photo");
            Uri u=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,v);if(u==null)throw new IOException("MediaStore insert failed");
            OutputStream o=getContentResolver().openOutputStream(u);if(o==null)throw new IOException("Output stream failed");sheet.compress(Bitmap.CompressFormat.JPEG,95,o);o.close();
            Toast.makeText(this,"Print sheet disimpan • "+lastSavedName,Toast.LENGTH_LONG).show();
        }catch(Exception e){Toast.makeText(this,"Gagal simpan: "+e.getMessage(),Toast.LENGTH_LONG).show();}
    }
    void share(){
        if(lastSavedName==null){save(); if(lastSavedName==null)return;}
        Toast.makeText(this,"Simpan dahulu, kemudian gunakan galeri untuk memilih print sheet dan berkongsi.",Toast.LENGTH_LONG).show();
    }
    void printSheet(){
        new AlertDialog.Builder(this).setTitle("Print / Send to printer").setMessage("Print sheet telah disediakan pada saiz "+paperW()+" × "+paperH()+" mm. Simpan dahulu, kemudian pilih fail ini daripada galeri/aplikasi pencetak yang menyokong JPEG.").setPositiveButton("Save now",(d,w)->save()).setNegativeButton("Close",null).show();
    }
}
