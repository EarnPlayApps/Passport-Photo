package com.earnplayapps.passportphoto;

import android.content.Context;
import android.provider.Settings;
import java.security.MessageDigest;

public final class ProManager {
    private static final String PREF="passport_photo_pro"; private static final String KEY="active"; private static final String INSTALL="installation";
    private ProManager(){}
    public static String installationId(Context c){String raw=Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);return sha256("Passport Photo:"+(raw==null?"unknown":raw));}
    public static boolean isPro(Context c){android.content.SharedPreferences p=c.getSharedPreferences(PREF,0);return p.getBoolean(KEY,false)&&installationId(c).equals(p.getString(INSTALL,""));}
    public static boolean activate(Context c,String code){if(code==null)return false;String id=installationId(c),expected=expectedCode(id);if(code.trim().equalsIgnoreCase(expected)){c.getSharedPreferences(PREF,0).edit().putBoolean(KEY,true).putString(INSTALL,id).apply();return true;}return false;}
    public static String expectedCode(String installationId){return sha256("PP19.90|"+installationId).substring(0,12).toUpperCase();}
    private static String sha256(String s){try{MessageDigest d=MessageDigest.getInstance("SHA-256");byte[] b=d.digest(s.getBytes("UTF-8"));StringBuilder x=new StringBuilder();for(byte v:b)x.append(String.format("%02x",v));return x.toString();}catch(Exception e){return "0000000000000000000000000000000000000000000000000000000000000000";}}
}
