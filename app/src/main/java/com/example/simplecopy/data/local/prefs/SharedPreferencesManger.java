package com.example.simplecopy.data.local.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;


public class SharedPreferencesManger {

    public static SharedPreferences sharedPreferences = null;
    public static final String USER_DATA = "USER_DATA";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
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

    public static void setSharedPreferences(Context activity) {
        if (sharedPreferences == null) {
            sharedPreferences = activity.getSharedPreferences (
                    "themePref", activity.MODE_PRIVATE);
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

    public static void SaveThemePref(boolean isDark, Activity activity) {
        setSharedPreferences (activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit ( );
            editor.putBoolean ("themePref", isDark);
            editor.apply ( );
        } else {
            setSharedPreferences (activity);
        }
    }

    public static boolean getThemePref(Context activity){
        setSharedPreferences (activity);

        return sharedPreferences.getBoolean ("themePref", false);
    }

    public static void SaveData(Activity activity, String data_Key, Object data_Value) {
        setSharedPreferences (activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit ( );
            Gson gson = new Gson ( );
            String StringData = gson.toJson (data_Value);
            editor.putString (data_Key, StringData);
            editor.apply ( );
        }
    }

    public static void SaveData(Activity activity, String data_Key, HashMap<String, String> jsonMap) {
        setSharedPreferences (activity);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit ( );
            Gson gson = new Gson ( );
            String jsonString = gson.toJson (jsonMap);
            editor.putString (data_Key, jsonString);
            editor.apply ( );
        }
    }

    public static HashMap<String, String> LoadHashMap (Activity activity, String data_Key) {
        setSharedPreferences (activity);

//        String defValue = new Gson ().toJson(new HashMap<String, List<String>>());
//        String json = sharedPreferences.getString(data_Key, defValue);
//        TypeToken<HashMap<String,List<String>>> token = new TypeToken<HashMap<String,List<String>>>() {};
//        HashMap<String,List<String>> retrievedMap = new Gson().fromJson(json,token.getType());


        //get from shared prefs
        Gson gson = new Gson ( );
        String storedHashMapString = sharedPreferences.getString(data_Key, data_Key);
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> retrievedMap = gson.fromJson(storedHashMapString, type);

        return retrievedMap;
    }

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
