package com.earnplayapps.passportphoto;

import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Small local project-history store. Keeps insertion order instead of relying on HashSet ordering. */
public final class ProjectStore {
    private static final String PREF = "passport_photo_projects";
    private static final String KEY = "items_ordered";
    private static final String LEGACY_KEY = "items";
    private ProjectStore() {}

    public static synchronized void add(Context c, String name) {
        if (name == null || name.trim().isEmpty()) return;
        Set<String> values = read(c);
        values.remove(name);
        LinkedHashSet<String> next = new LinkedHashSet<>();
        next.add(name.trim());
        next.addAll(values);
        write(c, next);
    }

    public static synchronized ArrayList<String> all(Context c) {
        return new ArrayList<>(read(c));
    }

    public static synchronized void remove(Context c, String name) {
        if (name == null) return;
        Set<String> values = read(c);
        if (values.remove(name)) write(c, values);
    }

    public static synchronized void clear(Context c) {
        c.getSharedPreferences(PREF, 0).edit().remove(KEY).remove(LEGACY_KEY).apply();
    }

    private static LinkedHashSet<String> read(Context c) {
        android.content.SharedPreferences p = c.getSharedPreferences(PREF, 0);
        LinkedHashSet<String> out = new LinkedHashSet<>();
        String packed = p.getString(KEY, "");
        if (packed != null && !packed.isEmpty()) {
            for (String s : packed.split("\\n", -1)) if (!s.trim().isEmpty()) out.add(s);
            if (!out.isEmpty()) return out;
        }
        Set<String> legacy = p.getStringSet(LEGACY_KEY, null);
        if (legacy != null) {
            List<String> list = new ArrayList<>(legacy);
            java.util.Collections.sort(list, java.util.Collections.reverseOrder());
            out.addAll(list);
            if (!out.isEmpty()) write(c, out);
        }
        return out;
    }

    private static void write(Context c, Set<String> values) {
        StringBuilder b = new StringBuilder();
        for (String s : values) {
            if (s == null || s.trim().isEmpty()) continue;
            if (b.length() > 0) b.append('\n');
            b.append(s.trim().replace("\n", " "));
        }
        c.getSharedPreferences(PREF, 0).edit().putString(KEY, b.toString()).remove(LEGACY_KEY).apply();
    }
}
