package com.example.simplecopy.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simplecopy.R;
import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.utils.HelperMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LANG_NUM;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.LoadData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.SaveData;
import static com.example.simplecopy.data.local.prefs.SharedPreferencesManger.USER_LANG;

public class SettingsActivity extends AppCompatActivity {


    @BindView(R.id.settings_activity_tv_lang)
    TextView settingsActivityTvLang;
    @BindView(R.id.settings_activity_lay_select_lang)
    LinearLayout settingsActivityLaySelectLang;
    private int lanChoose = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethods.changeLang (this, SharedPreferencesManger.onLoadLang (this));
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_settings);
        ButterKnife.bind (this);
//        assert getSupportActionBar() != null;   //null check
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        if (LoadData (this, LANG_NUM) != null) {
            lanChoose = Integer.parseInt (LoadData (this, LANG_NUM));
            if (lanChoose == 1) {
                settingsActivityTvLang.setText (getResources ( ).getString (R.string.english_key));
            } else {
                settingsActivityTvLang.setText (getResources ( ).getString (R.string.arabic_key));
            }
        } else {
            settingsActivityTvLang.setText (getResources ( ).getString (R.string.arabic_key));
        }

    }


    private void showSelectLanguageDialog() {

        final String[] lanList = {getResources ( ).getString (R.string.arabic_key), getResources ( ).getString (R.string.english_key)};
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle (R.string.language);

        if (LoadData (this, LANG_NUM) != null) {
            lanChoose = Integer.parseInt (LoadData (this, LANG_NUM));
        }

        builder.setSingleChoiceItems (lanList, lanChoose, new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //Arabic
                    if (LoadData (SettingsActivity.this, LANG_NUM) != null) {
                        if (Integer.parseInt (LoadData (SettingsActivity.this, LANG_NUM)) == 0) {
                            dialog.dismiss ( );
                            return;
                        }
                    }
                    SaveData (SettingsActivity.this, USER_LANG, "ar");
                } else if (which == 1) {
                    //English
                    if (LoadData (SettingsActivity.this, LANG_NUM) != null) {
                        if (Integer.parseInt (LoadData (SettingsActivity.this, LANG_NUM)) == 1) {
                            dialog.dismiss ( );
                            return;
                        }
                    }
                    SaveData (SettingsActivity.this, USER_LANG, "en");
                }
                SaveData (SettingsActivity.this, LANG_NUM, Integer.toString (which));
                dialog.dismiss ( );
                startActivity (new Intent (SettingsActivity.this, MainActivity.class));
                finish ( );
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ( );
        alertDialog.show ( );
    }

    @OnClick(R.id.settings_activity_lay_select_lang)
    public void onViewClicked() {
        showSelectLanguageDialog ( );
    }

//    @Override
//    public boolean onSupportNavigateUp(){
//        finish();
//        return true;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ( );
        startActivity (new Intent (SettingsActivity.this, MainActivity.class));
        finish ( );

    }
}
