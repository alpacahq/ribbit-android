package com.stockly.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.stockly.android.BuildConfig;

/**
 * A utility class provides option to save preferences of different
 * types and functions to perform save and delete shared preferences.
 */
final public class PrefUtils {
    private static final String PREF_NAME = BuildConfig.APPLICATION_ID + "_Pref_";
    private static final String PREF_NON_DELETABLE_NAME = BuildConfig.APPLICATION_ID + "_Pref_Non_Del";

    private PrefUtils() {

    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getNonDelPreferences(Context context) {
        return context.getSharedPreferences(PREF_NON_DELETABLE_NAME, Context.MODE_PRIVATE);
    }

    public static boolean setBoolean(Context context, String key, boolean value) {
        return getPreferences(context).edit().putBoolean(key, value).commit();
    }

    public static boolean setNonDeleteAbleBoolean(Context context, String key, boolean value) {
        return getNonDelPreferences(context).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        return getPreferences(context).getBoolean(key, false);
    }

    public static boolean getNonDeleteAbleBoolean(Context context, String key) {
        return getNonDelPreferences(context).getBoolean(key, false);
    }

    public static void setString(Context context, String key, String value) {
        getPreferences(context).edit().putString(key, value).apply();
    }

    public static void setNonDeleteAbleString(Context context, String key, String value) {
        getNonDelPreferences(context).edit().putString(key, value).apply();
    }

    public static void clearStringData(Context context, String key) {
        getPreferences(context).edit().putString(key, null).apply();
    }

    public static void clearNonDeleteAbleStringData(Context context, String key) {
        getNonDelPreferences(context).edit().putString(key, null).apply();
    }

    public static String getString(Context context, String key) {
        return getPreferences(context).getString(key, null);
    }

    public static String getString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static String getNonDeleteAbleString(Context context, String key) {
        return getNonDelPreferences(context).getString(key, null);
    }

    public static String getNonDeleteAbleString(Context context, String key, String defValue) {
        return getNonDelPreferences(context).getString(key, defValue);
    }

    public static void setLong(Context context, String key, long value) {
        getPreferences(context).edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key) {
        return getPreferences(context).getLong(key, 0);
    }

    public static void setNonDeleteAbleLong(Context context, String key, long value) {
        getNonDelPreferences(context).edit().putLong(key, value).apply();
    }

    public static long getNonDeleteAbleLong(Context context, String key) {
        return getNonDelPreferences(context).getLong(key, 0);
    }

    public static void setInt(Context context, String key, int value) {
        getPreferences(context).edit().putInt(key, value).apply();
    }

    public static long getInt(Context context, String key) {
        return getPreferences(context).getInt(key, 0);
    }

    public static void setNonDeleteAbleInt(Context context, String key, int value) {
        getNonDelPreferences(context).edit().putInt(key, value).apply();
    }

    public static long getNonDeleteAbleInt(Context context, String key) {
        return getNonDelPreferences(context).getInt(key, 0);
    }

    public static void clear(Context context) {
        getPreferences(context).edit().clear().apply();
    }

}
