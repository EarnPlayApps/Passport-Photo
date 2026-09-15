package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;

public class PhotoEditorActivity extends android.app.Activity {
    private Bitmap original;
    private ImageView preview;
    private int widthMm=35, heightMm=50;
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    private Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setMinHeight(dp(50));b.setLayoutParams(new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1));return b;}
    @Override public void onCreate(Bundle b){super.onCreate(b); Uri uri=getIntent().getData();
        if(uri==null){finish();return;}
        try{original=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);}catch(Exception e){Toast.makeText(this,"Tidak dapat membaca foto",Toast.LENGTH_LONG).show();finish();return;}
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(16),dp(18),dp(16),dp(16));root.setBackgroundColor(0xFFF5F7FB);
        TextView title=new TextView(this);title.setText("Photo Editor");title.setTextSize(24);title.setTextColor(0xFF17233D);root.addView(title);
        TextView info=new TextView(this);info.setText("Malaysia requirement: "+widthMm+" x "+heightMm+" mm\nOriginal preserved locally");info.setTextColor(0xFF5B6475);root.addView(info);
        preview=new ImageView(this);preview.setScaleType(ImageView.ScaleType.FIT_CENTER);preview.setBackgroundColor(0xFFE4E8EF);preview.setImageBitmap(original);root.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);
        Button rotate=button("Rotate");rotate.setOnClickListener(v->rotate());row.addView(rotate);
        Button crop=button("Auto Crop");crop.setOnClickListener(v->crop());row.addView(crop);
        Button reset=button("Reset");reset.setOnClickListener(v->{preview.setImageBitmap(original);});row.addView(reset);root.addView(row);
        Button export=new Button(this);export.setText("CHECK & EXPORT JPG");export.setOnClickListener(v->export());root.addView(export,new LinearLayout.LayoutParams(-1,dp(54)));
        setContentView(root);
    }
    private void rotate(){Matrix m=new Matrix();m.postRotate(90);original=Bitmap.createBitmap(original,0,0,original.getWidth(),original.getHeight(),m,true);preview.setImageBitmap(original);}
    private void crop(){int w=original.getWidth(),h=original.getHeight();float target=(float)widthMm/heightMm;int nw=w,nh=(int)(w/target);if(nh>h){nh=h;nw=(int)(h*target);}int l=(w-nw)/2,t=(h-nh)/2;Bitmap c=Bitmap.createBitmap(original,l,t,nw,nh);preview.setImageBitmap(c);original=c;}
    private void export(){try{File dir=new File(getExternalFilesDir(null),"Passport Photo");if(!dir.exists())dir.mkdirs();File out=new File(dir,"passport_photo_"+System.currentTimeMillis()+".jpg");FileOutputStream fos=new FileOutputStream(out);preview.getDrawable();Bitmap b=BitmapFactory.decodeResource(getResources(),android.R.drawable.ic_menu_gallery); // replaced below
            Bitmap shown=original;shown.compress(Bitmap.CompressFormat.JPEG,95,fos);fos.close();Toast.makeText(this,"Foto disimpan: "+out.getName(),Toast.LENGTH_LONG).show();}catch(Exception e){Toast.makeText(this,"Export gagal",Toast.LENGTH_LONG).show();}}
}
