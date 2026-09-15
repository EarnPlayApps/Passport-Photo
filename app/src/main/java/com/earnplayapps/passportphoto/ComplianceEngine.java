package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public final class ComplianceEngine {
    public static final class Issue { public final String title,detail,status; Issue(String t,String d,String s){title=t;detail=d;status=s;} }
    public static final class Report { public final List<Issue> issues; public final boolean pass; public final String summary; Report(List<Issue> i){issues=i; boolean fail=false; for(Issue x:i)if("FAIL".equals(x.status))fail=true;pass=!fail;summary=pass?"PASS — READY TO EXPORT":"FAIL — FIX PHOTO";} }
    private ComplianceEngine(){}
    public static Report check(Bitmap b, Requirement r){
        List<Issue> out=new ArrayList<>();
        if(b==null){out.add(new Issue("Photo","No photo loaded","FAIL"));return new Report(out);}
        FaceAnalyzer.Result f=FaceAnalyzer.analyze(b);
        if(!f.found) out.add(new Issue("Face","No clear face detected. Use a front-facing photo.","FAIL"));
        else if(f.count>1) out.add(new Issue("Multiple faces","More than one face detected. Select/crop the correct person.","FAIL"));
        else { if(f.confidence<0.70f)out.add(new Issue("Head angle","Face appears tilted. Face the camera directly.","WARNING")); else out.add(new Issue("Face","Face detected and approximately front-facing.","PASS")); }
        if(r.widthMm>0&&r.heightMm>0)out.add(new Issue("Size",r.widthMm+" × "+r.heightMm+" mm requirement selected.","PASS")); else out.add(new Issue("Size","Official source does not specify an exact measurement. Do not invent one.","WARNING"));
        if(r.background!=null&&!r.background.equalsIgnoreCase("Tidak dinyatakan"))out.add(new Issue("Background","Required: "+r.background,"PASS"));
        int sample=0, white=0; for(int y=0;y<b.getHeight();y+=Math.max(1,b.getHeight()/20))for(int x=0;x<b.getWidth();x+=Math.max(1,b.getWidth()/20)){int p=b.getPixel(x,y);sample++; if(Color.red(p)>235&&Color.green(p)>235&&Color.blue(p)>235)white++;}
        if(r.background!=null&&r.background.toLowerCase().contains("putih")) { if(sample>0&&white/(float)sample<0.35f)out.add(new Issue("Background","Large parts of the sampled background are not white. Check background manually.","WARNING")); }
        if(b.getWidth()<600||b.getHeight()<600)out.add(new Issue("Resolution","Source image is relatively small; use the highest-quality original available.","WARNING")); else out.add(new Issue("Resolution","Source resolution is adequate for editing.","PASS"));
        return new Report(out);
    }
}
