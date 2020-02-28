package com.example.simplecopy.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.simplecopy.R;
import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.activity.user.UserActivity;
import com.example.simplecopy.utils.HelperMethods;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethods.changeLang(this, SharedPreferencesManger.onLoadLang(this));
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_splash_screen);

        new Timer ().schedule (new TimerTask ( ) {
            @Override
            public void run() {
                startActivity (new Intent (getApplicationContext (), UserActivity.class));
            }
        }, 1000);
    }
}
