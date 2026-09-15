package com.earnplayapps.passportphoto;

import android.content.Context;import java.util.*;
public final class ProjectStore{
 private static final String PREF="passport_photo_projects";private static final String KEY="items";private ProjectStore(){}
 public static synchronized void add(Context c,String name){if(name==null||name.trim().isEmpty())return;Set<String>s=new HashSet<>(c.getSharedPreferences(PREF,0).getStringSet(KEY,new HashSet<String>()));s.remove(name);s.add(name);c.getSharedPreferences(PREF,0).edit().putStringSet(KEY,s).apply();}
 public static synchronized ArrayList<String> all(Context c){ArrayList<String>a=new ArrayList<>(c.getSharedPreferences(PREF,0).getStringSet(KEY,new HashSet<String>()));Collections.sort(a,Collections.reverseOrder());return a;}
 public static synchronized void clear(Context c){c.getSharedPreferences(PREF,0).edit().remove(KEY).apply();}
}
