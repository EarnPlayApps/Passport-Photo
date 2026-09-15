package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.FaceDetector;

public final class FaceAnalyzer {
    public static final class Result { public final boolean found; public final int count; public final float eyeDistance; public final float midX,midY; public final float confidence;
        Result(boolean f,int c,float e,float x,float y,float cf){found=f;count=c;eyeDistance=e;midX=x;midY=y;confidence=cf;}
    }
    private FaceAnalyzer(){}
    public static Result analyze(Bitmap input){
        Bitmap b=input.getConfig()==Bitmap.Config.RGB_565?input:input.copy(Bitmap.Config.RGB_565,false);
        int max=900; float scale=Math.min(1f,max/(float)Math.max(b.getWidth(),b.getHeight()));
        if(scale<1f)b=Bitmap.createScaledBitmap(b,Math.max(1,Math.round(b.getWidth()*scale)),Math.max(1,Math.round(b.getHeight()*scale)),true);
        FaceDetector detector=new FaceDetector(b.getWidth(),b.getHeight(),5); FaceDetector.Face[] faces=new FaceDetector.Face[5]; int n=detector.findFaces(b,faces);
        if(n<=0)return new Result(false,0,0,b.getWidth()/2f,b.getHeight()/2f,0);
        FaceDetector.Face f=faces[0]; PointF p=new PointF(); f.getMidPoint(p); float eye=f.eyesDistance(); float angle=Math.abs(f.pose(FaceDetector.Face.EULER_Y))+Math.abs(f.pose(FaceDetector.Face.EULER_Z));
        float cf=Math.max(0,1f-angle/45f); return new Result(true,n,eye,p.x,p.y,cf);
    }
}
