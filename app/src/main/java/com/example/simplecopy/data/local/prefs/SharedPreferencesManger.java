package com.example.simplecopy.data.local.prefs;

import android.app.Activity;
import android.content.SharedPreferences;


public class SharedPreferencesManger {

    public static SharedPreferences sharedPreferences = null;
    public static final String USER_DATA = "USER_DATA";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_ID = "USER_ID";
    public static final String REMEMBER = "REMEMBER";
    public static final String REMEMBER_ME = "REMEMBER_ME";
    public static final String USER_LANG = "USER_LANG";
    public static final String LANG_NUM = "LANG_NUM";

    public static void setSharedPreferences(Activity activity) {
        if (sharedPreferences == null) {
            sharedPreferences = activity.getSharedPreferences (
                    "Blood", activity.MODE_PRIVATE);
        }
    }

    public static void SaveData(Activity activity, String data_Key, String data_Value) {
        setSharedPreferences (activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit ( );
            editor.putString (data_Key, data_Value);
            editor.apply ( );
        } else {
            setSharedPreferences (activity);
        }
    }

    public static void SaveData(Activity activity, String data_Key, boolean data_Value) {
        setSharedPreferences (activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit ( );
            editor.putBoolean (data_Key, data_Value);
            editor.apply ( );
        } else {
            setSharedPreferences (activity);
        }
    }

//    public static void SaveData(Activity activity, String data_Key, Object data_Value) {
//        setSharedPreferences (activity);
//        if (sharedPreferences != null) {
//            SharedPreferences.Editor editor = sharedPreferences.edit ( );
//            Gson gson = new Gson ( );
//            String StringData = gson.toJson (data_Value);
//            editor.putString (data_Key, StringData);
//            editor.apply ( );
//        }
//    }

    public static String LoadData(Activity activity, String data_Key) {
        setSharedPreferences (activity);

        return sharedPreferences.getString (data_Key, null);
    }

    public static boolean LoadBoolean(Activity activity, String data_Key) {
        setSharedPreferences (activity);

        return sharedPreferences.getBoolean (data_Key, false);
    }

//    public static LoginData loadUserData(Activity activity) {
//        LoginData userData = null;
//
//        Gson gson = new Gson ( );
//        userData = gson.fromJson (LoadData (activity, USER_DATA), LoginData.class);
//
//        return userData;
//    }

    public static void clean(Activity activity) {
        setSharedPreferences (activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit ( );
            editor.clear ( );
            editor.apply ( );
        }
    }

    public static String onLoadLang(Activity activity) {
        setSharedPreferences (activity);

        return sharedPreferences.getString (USER_LANG, "ar");
    }


}
