package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.*;

/** Requirement-aware local checker. Hard FAIL is used only for measurable blocking rules. */
public final class ComplianceEngine {
 public static final class Issue { public final String title,detail,status; Issue(String t,String d,String s){title=t;detail=d;status=s;} }
 public static final class Report { public final List<Issue> issues; public final boolean pass; public final String summary; Report(List<Issue> i){issues=i;boolean bad=false;for(Issue x:i)if("FAIL".equals(x.status))bad=true;pass=!bad;summary=pass?"PASS — READY TO EXPORT":"FIX REQUIRED — PHOTO NOT READY";} }
 private ComplianceEngine(){}
 public static Report check(Bitmap b,Requirement r){
  List<Issue> o=new ArrayList<>();
  if(b==null){o.add(new Issue("Photo","No photo loaded.","FAIL"));return new Report(o);}
  if(r==null){o.add(new Issue("Requirement","No requirement selected.","FAIL"));return new Report(o);}
  FaceAnalyzer.Result f=FaceAnalyzer.analyze(b);
  if(!f.found)o.add(new Issue("Face","No clear face detected. Use a front-facing photo with the whole face visible.","FAIL"));
  else if(f.count>1)o.add(new Issue("Multiple faces","More than one face was detected. Use one person per photo.","FAIL"));
  else{
   o.add(new Issue("Face","One face detected.","PASS"));
   o.add(new Issue("Head angle",f.confidence<.55f?"Face angle is uncertain; visually verify the final photo.":"Face orientation appears reasonably frontal.",f.confidence<.55f?"WARNING":"PASS"));
   float eye=f.eyeDistance/Math.max(1f,Math.min(b.getWidth(),b.getHeight()));
   o.add(new Issue("Face size",eye<.06f?"Face appears small in the source.":eye>.38f?"Face appears very large in the source; check margins.":"Face occupies a usable portion of the source.",eye<.06f||eye>.38f?"WARNING":"PASS"));
   float cx=f.midX/Math.max(1f,b.getWidth()),cy=f.midY/Math.max(1f,b.getHeight());
   o.add(new Issue("Horizontal position",cx<.20f||cx>.80f?"Face is far from centre. Use Auto Align or Crop.":"Face is near horizontal centre.",cx<.20f||cx>.80f?"WARNING":"PASS"));
   o.add(new Issue("Vertical position",cy<.10f||cy>.85f?"Face is near an image edge; check headroom and chin margin.":"Face position is within the usable image area.",cy<.10f||cy>.85f?"WARNING":"PASS"));
   addMeasuredRules(o,b,r,f);
  }
  if(r.widthMm>0&&r.heightMm>0){float rr=(float)r.widthMm/r.heightMm,ar=(float)b.getWidth()/Math.max(1,b.getHeight());if(Math.abs(rr-ar)/rr<=.01f)o.add(new Issue("Aspect ratio",r.widthMm+" × "+r.heightMm+" mm ratio matches.","PASS"));else o.add(new Issue("Aspect ratio","Current image ratio does not match "+r.widthMm+" × "+r.heightMm+" mm. Crop before export.","FAIL"));o.add(new Issue("Physical output","Final export target: "+PhotoEngine.mmToPx(r.widthMm)+" × "+PhotoEngine.mmToPx(r.heightMm)+" px at 300 DPI.","INFO"));}else o.add(new Issue("Size","Official source does not state an exact physical measurement. No size has been invented.","INFO"));
  if(r.pixelWidth>0&&r.pixelHeight>0)o.add(new Issue("Pixel dimensions",b.getWidth()==r.pixelWidth&&b.getHeight()==r.pixelHeight?"Source matches documented pixel dimensions.":"Final export will produce documented "+r.pixelWidth+" × "+r.pixelHeight+" px output.",b.getWidth()==r.pixelWidth&&b.getHeight()==r.pixelHeight?"PASS":"INFO"));
  String bg=r.background==null?"":r.background.toLowerCase(Locale.US);
  if(bg.contains("tidak dinyatakan")||bg.contains("unspecified")||bg.trim().isEmpty())o.add(new Issue("Background","Official source does not specify a background colour. No rule is assumed.","INFO"));
  else if(bg.contains("putih")){int n=0,w=0,sx=Math.max(1,b.getWidth()/24),sy=Math.max(1,b.getHeight()/24);for(int y=0;y<b.getHeight();y+=sy)for(int x=0;x<b.getWidth();x+=sx){int p=b.getPixel(x,y);n++;if(Color.red(p)>235&&Color.green(p)>235&&Color.blue(p)>235)w++;}float q=n==0?0:w/(float)n;o.add(new Issue("Background",q<.25f?"Sampled areas do not look predominantly white.":"White background appears plausible; check shadows and hair edges.",q<.25f?"WARNING":"PASS"));}
  else o.add(new Issue("Background","Required background: "+r.background+". Automated sampling cannot prove official compliance; verify visually.","INFO"));
  long pixels=(long)b.getWidth()*b.getHeight();o.add(new Issue("Resolution",b.getWidth()<600||b.getHeight()<600||pixels<360000L?"Source image is relatively small.":"Source resolution is adequate for editing.",b.getWidth()<600||b.getHeight()<600||pixels<360000L?"WARNING":"PASS"));
  double lum=averageLuminance(b);o.add(new Issue("Lighting",lum<55?"Image appears very dark.":lum>225?"Image appears very bright/washed out.":"Overall exposure is within a usable range.",lum<55||lum>225?"WARNING":"PASS"));
  validateFinalFile(o,b,r);
  if(r.format!=null&&!r.format.trim().isEmpty()&&!r.format.toLowerCase(Locale.US).contains("tidak dinyatakan"))o.add(new Issue("File format","Required output format: "+r.format+". Final export applies the permitted format.","INFO"));
  if(r.faceRules!=null&&!r.faceRules.trim().isEmpty())o.add(new Issue("Official face rules",r.faceRules,"INFO"));
  return new Report(o);
 }
 private static void addMeasuredRules(List<Issue> o,Bitmap b,Requirement r,FaceAnalyzer.Result f){if(r.widthMm<=0||r.heightMm<=0||f.eyeDistance<=0)return;RequirementRules.Rules q=RequirementRules.forRequirement(r);if(!q.any())return;float scale=r.heightMm/(float)Math.max(1,b.getHeight());float head=f.eyeDistance*2.4f*scale;float top=Math.max(0,(f.midY-f.eyeDistance*1.15f)*scale);float chin=Math.max(0,r.heightMm-(f.midY+f.eyeDistance*1.25f)*scale);if(q.headMin>0||q.headMax>0)addRange(o,"Estimated head height",head,q.headMin,q.headMax);if(q.topMin>0||q.topMax>0)addRange(o,"Estimated top margin",top,q.topMin,q.topMax);if(q.chinMin>0||q.chinMax>0)addRange(o,"Estimated chin margin",chin,q.chinMin,q.chinMax);if(q.faceMin>0||q.faceMax>0)addRange(o,"Estimated face size",head,q.faceMin,q.faceMax);}
 private static void addRange(List<Issue> o,String name,float v,float min,float max){boolean bad=(min>0&&v<min)||(max>0&&v>max);String range=(min>0?String.format(Locale.US,"%.1f",min):"open")+"–"+(max>0?String.format(Locale.US,"%.1f",max):"open")+" mm";o.add(new Issue(name,String.format(Locale.US,"Anggaran %.1f mm; julat rasmi %s. Ini panduan framing, bukan pengukuran biometrik tepat.",v,range),bad?"WARNING":"INFO"));}
 private static void validateFinalFile(List<Issue> o,Bitmap b,Requirement r){if(r.widthMm<=0||r.heightMm<=0)return;try{Bitmap out=PhotoEngine.resizeForPrintMm(b,r.widthMm,r.heightMm);String fmt=r.format==null?"":r.format.toLowerCase(Locale.US);String rule=r.maxFileSize==null?"":r.maxFileSize.toLowerCase(Locale.US).trim();long limit=parseLimitBytes(rule);if(limit>0&&fmt.contains("jpg")&&!fmt.contains("png")){if(rule.contains("minimum")){byte[] d=PhotoEngine.jpeg(out,100);if(d.length<limit)o.add(new Issue("Final file size","Final JPEG is "+formatBytes(d.length)+"; official minimum is "+formatBytes(limit)+".","FAIL"));else o.add(new Issue("Final file size","Final JPEG can meet the official minimum.","PASS"));}else{int chosen=-1;long bytes=-1;for(int q=95;q>=30;q-=5){byte[] d=PhotoEngine.jpeg(out,q);if(d.length<=limit){chosen=q;bytes=d.length;break;}}if(chosen<0)o.add(new Issue("Final file size","No tested JPEG quality can meet the official maximum of "+formatBytes(limit)+".","FAIL"));else o.add(new Issue("Final file size","Final JPEG can meet the official maximum: "+formatBytes(bytes)+" at quality "+chosen+".","PASS"));}}else if(limit>0)o.add(new Issue("Final file size","Rule: "+r.maxFileSize+"; automatic validation is limited for this format.","INFO"));}catch(Exception e){o.add(new Issue("Final output validation","Could not simulate final output: "+e.getMessage(),"WARNING"));}}
 private static long parseLimitBytes(String rule){try{String x=rule.replace(',','.');String n=x.replaceAll("[^0-9.]","");if(n.isEmpty())return-1;double v=Double.parseDouble(n);if(x.contains("mb"))return(long)(v*1048576d);if(x.contains("kb"))return(long)(v*1024d);}catch(Exception ignored){}return-1;}
 private static String formatBytes(long n){return n>=1048576?String.format(Locale.US,"%.2f MB",n/1048576d):String.format(Locale.US,"%.1f KB",n/1024d);}
 private static double averageLuminance(Bitmap b){int step=Math.max(1,Math.max(b.getWidth(),b.getHeight())/80);long total=0,count=0;for(int y=0;y<b.getHeight();y+=step)for(int x=0;x<b.getWidth();x+=step){int p=b.getPixel(x,y);total+=(299*Color.red(p)+587*Color.green(p)+114*Color.blue(p))/1000;count++;}return count==0?128:total/(double)count;}
 public static boolean hasBlockingIssues(Report r){if(r==null)return true;for(Issue i:r.issues)if("FAIL".equals(i.status))return true;return false;}
}
