package com.example.simplecopy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource (R.xml.pref_setting);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
    }

    public void saveLanguage(String lang) {
        String langPref = "Language";
        SharedPreferences sharedPreferences  = getActivity ().getSharedPreferences("com.example.simplecopy.pref_setting",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences .edit();
        editor.putString("USER_LANGUAGE", lang);
        editor.apply ();

    }

    public void loadLanguage() {

        SharedPreferences prefs = getActivity ().getSharedPreferences("MY_LANGUAGE", Activity.MODE_PRIVATE);
        //  String myLang = prefs.getString("myLanguage", "");

        if (prefs.getString("myLanguage", "").equals("en")) {
            //setLanguage("en");
        } else if (prefs.getString("myLanguage", "").equals("ar")) {
            //setLanguage("ar");
        }

    }
}
