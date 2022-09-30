package com.thisisnotyours.vehicleregistrationapp.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    public static final String PREFERENCE_NAME = "login_info";

    private static final String DEFAULT_VALUE_STRING = "";

    private static SharedPreferences getMPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /** String 값 저장
     * @param context
     * @param key
     * @param value
     * **/

    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getMPreference(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /** String 값 로드
     * @param context
     * @param key
     * @return
     **/

    public static String getString(Context context, String key) {
        SharedPreferences prefs = getMPreference(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }

    /** 키값 삭제
     * @param context
     * @param key
     **/

    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getMPreference(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    /** 모든 저장 데이터 삭제
     * @param context
     **/

    public static void clear(Context context) {
        SharedPreferences prefs = getMPreference(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

}
