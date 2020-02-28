package com.example.simplecopy.ui.activity.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.simplecopy.R;
import com.example.simplecopy.data.local.prefs.SharedPreferencesManger;
import com.example.simplecopy.ui.activity.BaseActivity;
import com.example.simplecopy.ui.activity.MainActivity;
import com.example.simplecopy.ui.fragment.user.LoginFragment;
import com.example.simplecopy.utils.HelperMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.simplecopy.utils.HelperMethods.replaceFragment;

public class UserActivity extends BaseActivity {
    //FireBase auth
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HelperMethods.changeLang(this, SharedPreferencesManger.onLoadLang(this));
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_user);

        replaceFragment (getSupportFragmentManager ( ), R.id.user_container, new LoginFragment ( ));
        firebaseAuth = FirebaseAuth.getInstance ();

    }

    @Override
    protected void onStart() {
        checkUserStatus ( );
        super.onStart ( );
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser ( );
        if (user != null) {
            //user is signed in, go to login activity
            startActivity (new Intent (UserActivity.this, MainActivity.class));
            finish ( );
        } else {
            //user not signed in stay here

        }
    }
}
