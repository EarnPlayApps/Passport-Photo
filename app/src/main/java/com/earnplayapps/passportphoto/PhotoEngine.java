package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import java.io.ByteArrayOutputStream;

public final class PhotoEngine {
    private PhotoEngine() {}
    public static Bitmap rotate(Bitmap src, float degrees) { Matrix m=new Matrix(); m.postRotate(degrees); return Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),m,true); }
    public static Bitmap cropRatio(Bitmap src, int widthMm, int heightMm) {
        if(widthMm<=0||heightMm<=0) return src;
        int w=src.getWidth(), h=src.getHeight(); float ratio=(float)widthMm/heightMm;
        int nw=w, nh=Math.round(w/ratio); if(nh>h){nh=h; nw=Math.round(h*ratio);} int l=(w-nw)/2, t=(h-nh)/2;
        return Bitmap.createBitmap(src,l,t,nw,nh);
    }
    /** Converts a physical print size to pixels using the app's 300 DPI output assumption. */
    public static int mmToPx(int mm){return Math.max(1,Math.round(mm*300f/25.4f));}
    public static Bitmap resizeForPrint(Bitmap src, int widthPx, int heightPx) { return Bitmap.createScaledBitmap(src,Math.max(1,widthPx),Math.max(1,heightPx),true); }
    public static Bitmap resizeForPrintMm(Bitmap src, int widthMm, int heightMm) {
        if(widthMm<=0||heightMm<=0)return src;
        Bitmap cropped=cropRatio(src,widthMm,heightMm);
        return resizeForPrint(cropped,mmToPx(widthMm),mmToPx(heightMm));
    }
    public static Bitmap adjust(Bitmap src, float brightness, float contrast) {
        Bitmap out=Bitmap.createBitmap(src.getWidth(),src.getHeight(),Bitmap.Config.ARGB_8888);
        float c=contrast; float factor=(259f*(c+255f))/(255f*(259f-c));
        for(int y=0;y<src.getHeight();y++) for(int x=0;x<src.getWidth();x++) {
            int p=src.getPixel(x,y); int r=clamp(Math.round(factor*(Color.red(p)-128)+128+brightness)); int g=clamp(Math.round(factor*(Color.green(p)-128)+128+brightness)); int b=clamp(Math.round(factor*(Color.blue(p)-128)+128+brightness)); out.setPixel(x,y,Color.argb(Color.alpha(p),r,g,b));
        }
        return out;
    }
    public static byte[] jpeg(Bitmap src,int quality){ByteArrayOutputStream out=new ByteArrayOutputStream();src.compress(Bitmap.CompressFormat.JPEG,quality,out);return out.toByteArray();}
    private static int clamp(int v){return Math.max(0,Math.min(255,v));}
}
