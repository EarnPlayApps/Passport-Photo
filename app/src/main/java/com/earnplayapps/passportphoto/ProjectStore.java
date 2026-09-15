package com.earnplayapps.passportphoto;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class ProjectStore {
    private static final String PREF="passport_photo_projects"; private static final String KEY="items";
    private ProjectStore(){}
    public static void add(Context c,String name){Set<String> s=new HashSet<>(c.getSharedPreferences(PREF,0).getStringSet(KEY,new HashSet<String>()));s.add(name);c.getSharedPreferences(PREF,0).edit().putStringSet(KEY,s).apply();}
    public static ArrayList<String> all(Context c){return new ArrayList<>(c.getSharedPreferences(PREF,0).getStringSet(KEY,new HashSet<String>()));}
    public static void clear(Context c){c.getSharedPreferences(PREF,0).edit().remove(KEY).apply();}
}
