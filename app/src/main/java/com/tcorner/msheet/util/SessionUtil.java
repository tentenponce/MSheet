package com.tcorner.msheet.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference Manager
 * Created by Tenten Ponce on 03/11/2017.
 */

public class SessionUtil {
    public static final String SENSOR_SETTINGS = "SENSOR_SETTINGS";
    private static final String APP_PREF = "com.tcorner.msheet";
    private static final int MODE = Context.MODE_PRIVATE;

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).apply();
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).apply();
    }

    public static void writeInt(Context context, String key, int value) {
        getEditor(context).putInt(key, value).apply();
    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static int readInt(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static boolean readBoolean(Context context, String key, boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(APP_PREF, MODE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void clearSession(Context context) {
        getEditor(context).clear().apply();
    }
}