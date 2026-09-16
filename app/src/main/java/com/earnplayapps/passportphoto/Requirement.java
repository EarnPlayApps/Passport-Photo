package com.earnplayapps.passportphoto;

public final class Requirement {
 public final String id,category,agency,state,institution,purpose,formName,applicant,photoRole,size,background,faceRules,format,maxFileSize,outputType,evidence,status,source,sourceDate,verifiedDate,measurementSource;
 public final int quantity,widthMm,heightMm,pixelWidth,pixelHeight;
 public final boolean mandatory;
 /** Optional measurable head/face constraints. Zero means the official source did not provide that measurement. */
 public final float headHeightMinMm,headHeightMaxMm,topMarginMinMm,topMarginMaxMm,chinMarginMinMm,chinMarginMaxMm,faceSizeMinMm,faceSizeMaxMm;

 public Requirement(String id,String category,String agency,String purpose,String applicant,String photoRole,int quantity,int widthMm,int heightMm,String size,String background,String faceRules,String format,String maxFileSize,String evidence,String status,String source){
  this(id,category,agency,"Malaysia","",purpose,"",applicant,photoRole,quantity,widthMm,heightMm,0,0,size,background,faceRules,format,maxFileSize,"digital/print",true,evidence,status,source,"", "", "",0,0,0,0,0,0,0,0);
 }

 public Requirement(String id,String category,String agency,String state,String institution,String purpose,String formName,String applicant,String photoRole,int quantity,int widthMm,int heightMm,int pixelWidth,int pixelHeight,String size,String background,String faceRules,String format,String maxFileSize,String outputType,String evidence,String status,String source,String sourceDate,String verifiedDate){
  this(id,category,agency,state,institution,purpose,formName,applicant,photoRole,quantity,widthMm,heightMm,pixelWidth,pixelHeight,size,background,faceRules,format,maxFileSize,outputType,true,evidence,status,source,sourceDate,verifiedDate,"",0,0,0,0,0,0,0,0);
 }

 public Requirement(String id,String category,String agency,String state,String institution,String purpose,String formName,String applicant,String photoRole,int quantity,int widthMm,int heightMm,int pixelWidth,int pixelHeight,String size,String background,String faceRules,String format,String maxFileSize,String outputType,boolean mandatory,String evidence,String status,String source,String sourceDate,String verifiedDate,String measurementSource){
  this(id,category,agency,state,institution,purpose,formName,applicant,photoRole,quantity,widthMm,heightMm,pixelWidth,pixelHeight,size,background,faceRules,format,maxFileSize,outputType,mandatory,evidence,status,source,sourceDate,verifiedDate,measurementSource,0,0,0,0,0,0,0,0);
 }

 public Requirement(String id,String category,String agency,String state,String institution,String purpose,String formName,String applicant,String photoRole,int quantity,int widthMm,int heightMm,int pixelWidth,int pixelHeight,String size,String background,String faceRules,String format,String maxFileSize,String outputType,boolean mandatory,String evidence,String status,String source,String sourceDate,String verifiedDate,String measurementSource,float headHeightMinMm,float headHeightMaxMm,float topMarginMinMm,float topMarginMaxMm,float chinMarginMinMm,float chinMarginMaxMm,float faceSizeMinMm,float faceSizeMaxMm){
  this.id=id;this.category=category;this.agency=agency;this.state=state;this.institution=institution;this.purpose=purpose;this.formName=formName;this.applicant=applicant;this.photoRole=photoRole;this.quantity=quantity;this.widthMm=widthMm;this.heightMm=heightMm;this.pixelWidth=pixelWidth;this.pixelHeight=pixelHeight;this.size=size;this.background=background;this.faceRules=faceRules;this.format=format;this.maxFileSize=maxFileSize;this.outputType=outputType;this.mandatory=mandatory;this.evidence=evidence;this.status=status;this.source=source;this.sourceDate=sourceDate;this.verifiedDate=verifiedDate;this.measurementSource=measurementSource;this.headHeightMinMm=headHeightMinMm;this.headHeightMaxMm=headHeightMaxMm;this.topMarginMinMm=topMarginMinMm;this.topMarginMaxMm=topMarginMaxMm;this.chinMarginMinMm=chinMarginMinMm;this.chinMarginMaxMm=chinMarginMaxMm;this.faceSizeMinMm=faceSizeMinMm;this.faceSizeMaxMm=faceSizeMaxMm;
 }
}
