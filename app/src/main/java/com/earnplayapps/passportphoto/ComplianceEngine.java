package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.*;

/** Requirement-aware checker. FAIL is reserved for measurable hard violations. */
public final class ComplianceEngine {
 public static final class Issue { public final String title,detail,status; Issue(String t,String d,String s){title=t;detail=d;status=s;} }
 public static final class Report {
  public final List<Issue> issues; public final boolean pass; public final String summary;
  Report(List<Issue> i){issues=i;boolean blocked=false;for(Issue x:i)if("FAIL".equals(x.status))blocked=true;pass=!blocked;summary=pass?"PASS — READY TO EXPORT":"FIX REQUIRED — PHOTO NOT READY";}
 }
 private ComplianceEngine(){}
 public static Report check(Bitmap b,Requirement r){
  List<Issue> o=new ArrayList<>();
  if(b==null){o.add(new Issue("Photo","No photo loaded.","FAIL"));return new Report(o);}
  if(r==null){o.add(new Issue("Requirement","No requirement selected. Choose an official profile or use Custom Size with the actual form specification.","FAIL"));return new Report(o);}
  FaceAnalyzer.Result f=FaceAnalyzer.analyze(b);
  if(!f.found)o.add(new Issue("Face","No clear face detected. Use a front-facing photo with the whole face visible.","FAIL"));
  else if(f.count>1)o.add(new Issue("Multiple faces","More than one face was detected. Use one person per photo.","FAIL"));
  else{
   o.add(new Issue("Face","One face detected.","PASS"));
   if(f.confidence<.55f)o.add(new Issue("Head angle","Face angle is uncertain. Retake or manually verify the final photo.","WARNING"));else o.add(new Issue("Head angle","Face orientation appears reasonably frontal.","PASS"));
   float eyeRatio=f.eyeDistance/Math.max(1f,Math.min(b.getWidth(),b.getHeight()));
   if(eyeRatio<.06f)o.add(new Issue("Face size","Face appears small in the source. Use a higher-quality original or move closer.","WARNING"));
   else if(eyeRatio>.38f)o.add(new Issue("Face size","Face appears very large in the source. Check headroom and chin margin.","WARNING"));
   else o.add(new Issue("Face size","Face occupies a usable portion of the source.","PASS"));
   float cx=f.midX/Math.max(1f,b.getWidth()),cy=f.midY/Math.max(1f,b.getHeight());
   if(cx<.20f||cx>.80f)o.add(new Issue("Horizontal position","Face is far from centre. Use Auto Align or Crop.","WARNING"));else o.add(new Issue("Horizontal position","Face is near the horizontal centre.","PASS"));
   if(cy<.10f||cy>.85f)o.add(new Issue("Vertical position","Face is near an image edge. Check headroom and chin margin.","WARNING"));else o.add(new Issue("Vertical position","Face position is within the usable image area.","PASS"));
  }
  if(r.widthMm>0&&r.heightMm>0){
   float rr=(float)r.widthMm/r.heightMm,ar=(float)b.getWidth()/Math.max(1,b.getHeight());
   if(Math.abs(rr-ar)/rr<=.01f)o.add(new Issue("Aspect ratio",r.widthMm+" × "+r.heightMm+" mm ratio matches the selected requirement.","PASS"));
   else o.add(new Issue("Aspect ratio","Current image ratio does not match "+r.widthMm+" × "+r.heightMm+" mm. Crop the image before export.","FAIL"));
   o.add(new Issue("Physical output","Final export target: "+PhotoEngine.mmToPx(r.widthMm)+" × "+PhotoEngine.mmToPx(r.heightMm)+" px at 300 DPI.","INFO"));
  }else o.add(new Issue("Size","Official source does not state an exact physical measurement. No size has been invented.","INFO"));
  if(r.pixelWidth>0&&r.pixelHeight>0){if(b.getWidth()==r.pixelWidth&&b.getHeight()==r.pixelHeight)o.add(new Issue("Pixel dimensions","Source matches documented pixel dimensions.","PASS"));else o.add(new Issue("Pixel dimensions","Export will produce documented "+r.pixelWidth+" × "+r.pixelHeight+" px output.","INFO"));}
  String bg=r.background==null?"":r.background.toLowerCase(Locale.US);
  if(bg.contains("tidak dinyatakan")||bg.contains("unspecified")||bg.trim().isEmpty())o.add(new Issue("Background","Official source does not specify a background colour. No rule is assumed.","INFO"));
  else if(bg.contains("putih")){int sample=0,white=0,sx=Math.max(1,b.getWidth()/24),sy=Math.max(1,b.getHeight()/24);for(int y=0;y<b.getHeight();y+=sy)for(int x=0;x<b.getWidth();x+=sx){int p=b.getPixel(x,y);sample++;if(Color.red(p)>235&&Color.green(p)>235&&Color.blue(p)>235)white++;}float ratio=sample==0?0:white/(float)sample;if(ratio<.25f)o.add(new Issue("Background","Sampled areas do not look predominantly white. Replace or clean the background.","WARNING"));else o.add(new Issue("Background","White background appears plausible; visually check shadows and hair edges.","PASS"));}
  else o.add(new Issue("Background","Required background: "+r.background+". Automated sampling cannot prove official compliance; verify visually.","INFO"));
  long pixels=(long)b.getWidth()*b.getHeight();if(b.getWidth()<600||b.getHeight()<600||pixels<360000L)o.add(new Issue("Resolution","Source image is relatively small. Use the highest-quality original available.","WARNING"));else o.add(new Issue("Resolution","Source resolution is adequate for editing.","PASS"));
  double lum=averageLuminance(b);if(lum<55)o.add(new Issue("Lighting","Image appears very dark. Improve lighting.","WARNING"));else if(lum>225)o.add(new Issue("Lighting","Image appears very bright/washed out. Check exposure.","WARNING"));else o.add(new Issue("Lighting","Overall exposure is within a usable range.","PASS"));
  if(r.format!=null&&!r.format.trim().isEmpty()&&!r.format.toLowerCase(Locale.US).contains("tidak dinyatakan"))o.add(new Issue("File format","Required output format: "+r.format+". Final export applies the permitted format.","INFO"));
  if(r.maxFileSize!=null&&!r.maxFileSize.trim().isEmpty()&&!r.maxFileSize.toLowerCase(Locale.US).contains("tidak dinyatakan"))o.add(new Issue("File size","Documented file-size rule: "+r.maxFileSize+". Final export should validate the encoded file.","INFO"));
  if(r.faceRules!=null&&!r.faceRules.trim().isEmpty())o.add(new Issue("Official face rules",r.faceRules,"INFO"));
  return new Report(o);
 }
 private static double averageLuminance(Bitmap b){int step=Math.max(1,Math.max(b.getWidth(),b.getHeight())/80);long total=0,count=0;for(int y=0;y<b.getHeight();y+=step)for(int x=0;x<b.getWidth();x+=step){int p=b.getPixel(x,y);total+=(299*Color.red(p)+587*Color.green(p)+114*Color.blue(p))/1000;count++;}return count==0?128:total/(double)count;}
 public static boolean hasBlockingIssues(Report r){if(r==null)return true;for(Issue i:r.issues)if("FAIL".equals(i.status))return true;return false;}
}
