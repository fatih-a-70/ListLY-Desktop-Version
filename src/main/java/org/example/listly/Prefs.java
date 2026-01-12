package org.example.listly;

import java.util.prefs.Preferences;

public class Prefs {
    private static final Preferences PREF = Preferences.userRoot().node("listly");

    public static void put(String k, String v) { PREF.put(k, v); }
    public static String get(String k, String def) { return PREF.get(k, def); }

    public static void putInt(String k, int v) { PREF.putInt(k, v); }
    public static int getInt(String k, int def) { return PREF.getInt(k, def); }

    public static void putBool(String k, boolean v) { PREF.putBoolean(k, v); }
    public static boolean getBool(String k, boolean def) { return PREF.getBoolean(k, def); }
}
