package com.earnplayapps.passportphoto;

public final class Requirement {
    public final String id, category, agency, purpose, applicant, photoRole, size, background, faceRules, format, maxFileSize, evidence, status, source;
    public final int widthMm, heightMm, quantity;
    public Requirement(String id, String category, String agency, String purpose, String applicant, String photoRole, int quantity, int widthMm, int heightMm, String size, String background, String faceRules, String format, String maxFileSize, String evidence, String status, String source) {
        this.id=id; this.category=category; this.agency=agency; this.purpose=purpose; this.applicant=applicant; this.photoRole=photoRole; this.quantity=quantity; this.widthMm=widthMm; this.heightMm=heightMm; this.size=size; this.background=background; this.faceRules=faceRules; this.format=format; this.maxFileSize=maxFileSize; this.evidence=evidence; this.status=status; this.source=source;
    }
}
