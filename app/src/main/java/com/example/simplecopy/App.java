package com.example.simplecopy;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.utils.ThemeHelper;

public class App extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        boolean themePref = SharedPreferencesManger.getThemePref (getContext ());
        ThemeHelper.applyTheme(themePref);

    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
