package com.dev.minhmin.locationchecker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Minh min on 1/4/2017.
 */

public class DataCenter {
    public static boolean running;
    private String userID = "";

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public static void registerDiviceKey(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Utils.DEVICE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Utils.DEVICE_KEY, key);
        editor.commit();
    }

    public static String getDeviceKey(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utils.DEVICE_KEY, Context.MODE_PRIVATE);
        return sp.getString(Utils.DEVICE_KEY, "");
    }

    public static void registerFirstTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utils.FIRST_TIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Utils.FIRST_TIME, false);
        editor.commit();
    }

    public static boolean isFirstTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utils.FIRST_TIME, Context.MODE_PRIVATE);
        return sp.getBoolean(Utils.FIRST_TIME, true);
    }
}
