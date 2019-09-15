package com.example.simplecopy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

public class SettingsActivity extends AppCompatActivity {
    AppBarLayout appBarLayout;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_settings);
        setupActionBar ();


        if (findViewById (R.id.fragment_container) != null){
            if (savedInstanceState != null){
                return;

                //getFragmentManager ( ).beginTransaction ( ).add (R.id.fragment_container, SettingsFragment()).commit ();
            }
        }



    }

    public void setupActionBar(){
        appBarLayout = findViewById (R.id.appbar_settings);
        toolbar = findViewById (R.id.toolbar_settings);
        setSupportActionBar (toolbar);

        ActionBar actionBar = this.getSupportActionBar ();

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled (true);
        }
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Settings) + "</font>"));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask (this);
        }
        return super.onOptionsItemSelected (item);
    }


}
