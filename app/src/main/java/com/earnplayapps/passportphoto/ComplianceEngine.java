package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.*;

/** Requirement-aware local checks. A warning is reviewable, not an automatic rejection. */
public final class ComplianceEngine {
    public static final class Issue {
        public final String title, detail, status;
        Issue(String t,String d,String s){title=t;detail=d;status=s;}
    }
    public static final class Report {
        public final List<Issue> issues;
        public final boolean pass;
        public final String summary;
        Report(List<Issue> i){
            issues=i; boolean fail=false;
            for(Issue x:i) if("FAIL".equals(x.status)) fail=true;
            pass=!fail;
            summary=pass?"PASS — READY TO EXPORT":"FAIL — FIX PHOTO BEFORE EXPORT";
        }
    }
    private ComplianceEngine(){}

    public static Report check(Bitmap b, Requirement r){
        List<Issue> o=new ArrayList<>();
        if(b==null){o.add(new Issue("Photo","No photo loaded.","FAIL"));return new Report(o);}
        if(r==null){o.add(new Issue("Requirement","No documented requirement selected. Use Custom Size for a form-specific specification.","FAIL"));return new Report(o);}

        FaceAnalyzer.Result f=FaceAnalyzer.analyze(b);
        if(!f.found)o.add(new Issue("Face","No clear face detected. Use a front-facing photo with the face visible.","FAIL"));
        else if(f.count>1)o.add(new Issue("Multiple faces","More than one face detected. Crop to the intended applicant.","FAIL"));
        else if(f.confidence<.70f)o.add(new Issue("Face angle","Face angle is uncertain. Reposition the subject and visually verify the final photo.","WARNING"));
        else o.add(new Issue("Face","One face detected; front-facing estimate is acceptable.","PASS"));

        if(r.widthMm>0&&r.heightMm>0){
            float target=(float)r.widthMm/r.heightMm, actual=(float)b.getWidth()/b.getHeight();
            if(Math.abs(target-actual)/target<=.01f)o.add(new Issue("Aspect ratio",r.widthMm+" × "+r.heightMm+" mm ratio matches.","PASS"));
            else o.add(new Issue("Aspect ratio","Current crop does not match "+r.widthMm+" × "+r.heightMm+" mm. Use Crop/Auto Align.","FAIL"));
            o.add(new Issue("Physical output","Export will resize to the selected physical dimensions at 300 DPI.","INFO"));
        } else o.add(new Issue("Size","Official source does not state an exact measurement. No dimension has been assumed.","INFO"));

        if(r.pixelWidth>0&&r.pixelHeight>0){
            if(b.getWidth()==r.pixelWidth&&b.getHeight()==r.pixelHeight)o.add(new Issue("Pixel dimensions","Exact pixel dimensions match.","PASS"));
            else o.add(new Issue("Pixel dimensions","Export will resize to "+r.pixelWidth+" × "+r.pixelHeight+" px.","INFO"));
        }

        String bg=r.background==null?"":r.background.toLowerCase(Locale.ROOT).trim();
        if(bg.isEmpty()||bg.contains("tidak dinyatakan")||bg.contains("unspecified")){
            o.add(new Issue("Background","No exact background rule was stated by this source. No rule is assumed.","INFO"));
        } else if(bg.contains("putih")){
            int sample=0,white=0,sx=Math.max(1,b.getWidth()/24),sy=Math.max(1,b.getHeight()/24);
            for(int y=0;y<b.getHeight();y+=sy) for(int x=0;x<b.getWidth();x+=sx){
                int p=b.getPixel(x,y);sample++;
                if(Color.red(p)>235&&Color.green(p)>235&&Color.blue(p)>235)white++;
            }
            if(sample>0&&white/(float)sample<.35f)o.add(new Issue("Background","Sampled areas are not predominantly white. Replace/check background.","WARNING"));
            else o.add(new Issue("Background","White background appears plausible from sampling; inspect edges and shadows.","PASS"));
        } else o.add(new Issue("Background","Required background: "+r.background+". Visual verification is still required.","INFO"));

        if(b.getWidth()<600||b.getHeight()<600)o.add(new Issue("Resolution","Source is relatively small. Prefer the highest-quality original photo.","WARNING"));
        else o.add(new Issue("Resolution","Source resolution is adequate for editing.","PASS"));

        if(r.format!=null&&!r.format.trim().isEmpty()&&!r.format.toLowerCase(Locale.ROOT).contains("tidak dinyatakan"))o.add(new Issue("Format","Required output: "+r.format+". Export will use a permitted encoder.","INFO"));
        if(r.maxFileSize!=null&&!r.maxFileSize.trim().isEmpty()&&!r.maxFileSize.toLowerCase(Locale.ROOT).contains("tidak dinyatakan"))o.add(new Issue("File size","Official rule: "+r.maxFileSize+". Final encoded file must be checked.","INFO"));
        if(r.faceRules!=null&&!r.faceRules.trim().isEmpty())o.add(new Issue("Official face rules",r.faceRules,"INFO"));
        o.add(new Issue("Acceptance","This checker assists preparation; final acceptance remains with the relevant agency/institution.","INFO"));
        return new Report(o);
    }
    public static boolean hasBlockingIssues(Report r){return r==null||!r.pass;}
}
