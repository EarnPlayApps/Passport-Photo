package com.earnplayapps.passportphoto;

/** Centralized measurable rules that are explicitly documented by the selected official profile. */
public final class RequirementRules {
    public static final class Rules {
        public final float headMin, headMax, topMin, topMax, chinMin, chinMax, faceMin, faceMax;
        Rules(float hm,float hx,float tm,float tx,float cm,float cx,float fm,float fx){headMin=hm;headMax=hx;topMin=tm;topMax=tx;chinMin=cm;chinMax=cx;faceMin=fm;faceMax=fx;}
        boolean any(){return headMin>0||headMax>0||topMin>0||topMax>0||chinMin>0||chinMax>0||faceMin>0||faceMax>0;}
    }
    private RequirementRules(){}
    public static Rules forRequirement(Requirement r){
        if(r==null)return new Rules(0,0,0,0,0,0,0,0);
        if(r.headHeightMinMm>0||r.headHeightMaxMm>0||r.topMarginMinMm>0||r.topMarginMaxMm>0||r.chinMarginMinMm>0||r.chinMarginMaxMm>0||r.faceSizeMinMm>0||r.faceSizeMaxMm>0)
            return new Rules(r.headHeightMinMm,r.headHeightMaxMm,r.topMarginMinMm,r.topMarginMaxMm,r.chinMarginMinMm,r.chinMarginMaxMm,r.faceSizeMinMm,r.faceSizeMaxMm);
        String id=r.id==null?"":r.id;
        // ESD official photo page: head 30–35 mm, top margin about 5 mm, chin margin 5–10 mm.
        if("MY-ESD-MYVISA-VTR".equals(id)||"MY-ESD-MYVISA-VDR".equals(id))
            return new Rules(30,35,5,5,5,10,0,0);
        // Immigration passport page explicitly gives 25–30 mm face size for children below 4.
        if("MY-PASSPORT-CHILD-U4".equals(id))
            return new Rules(0,0,0,0,0,0,25,30);
        return new Rules(0,0,0,0,0,0,0,0);
    }
}
