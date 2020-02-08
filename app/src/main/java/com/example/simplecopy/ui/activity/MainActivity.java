package com.example.simplecopy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.simplecopy.ui.activity.NoteEditor.NoteEditorActivity;
import com.example.simplecopy.ui.activity.NumberEditor.NumberEditorActivity;
import com.example.simplecopy.ui.activity.user.UserActivity;
import com.example.simplecopy.ui.fragment.Notes.NoteListFragment;
import com.example.simplecopy.ui.fragment.MainNumbers.Numbers.NumberListFragment;
import com.example.simplecopy.ui.fragment.MainNumbers.Daily.DailyWalletListFragment;
import com.example.simplecopy.R;
import com.example.simplecopy.adapters.viewPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    private long backPressedTime;
    private Toast backToast;

    TabLayout tabLayout;
    AppBarLayout appBarLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    private FloatingActionButton fab;

    //FireBase auth
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById (R.id.tabLayout);
        appBarLayout = findViewById (R.id.appbar_id);
        viewPager = findViewById (R.id.viewPager);
        toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.app_name) + "</font>"));

        firebaseAuth = FirebaseAuth.getInstance ();
        invalidateOptionsMenu();

        setupViewPager (viewPager);
        tabLayout.setupWithViewPager (viewPager);

        fab =  findViewById(R.id.fab);
        if (tabLayout.getSelectedTabPosition() == 0) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                    startActivity(intent);
                }
            });
        }
        viewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener ( ) {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab.show ();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;

                    case 1:
                        fab.show ();
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, NumberEditorActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;

                    default:
                        fab.hide ();
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager){
        viewPagerAdapter adapter = new viewPagerAdapter (getSupportFragmentManager ());
        adapter.AddFragment (new NoteListFragment (), getString(R.string.notes));
        adapter.AddFragment (new NumberListFragment (), getResources ().getString (R.string.Numbers));
        adapter.AddFragment (new DailyWalletListFragment (), getResources ().getString (R.string.Daily_Wallet));

        viewPager.setAdapter (adapter);
    }

//    @Override
//    protected void onStart() {
//        checkUserStatus ();
//        super.onStart ( );
//    }
//
//    private void checkUserStatus(){
//        FirebaseUser user = firebaseAuth.getCurrentUser ();
//        if (user != null){
//            //user is signed in stay here
//        } else {
//            //user not signed in, go to login activity
//            startActivity (new Intent (MainActivity.this, UserActivity.class));
//            finish ();
//        }
//    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis ()){
            backToast.cancel ();
            super.onBackPressed ( );
            return;
        } else {
            backToast = Toast.makeText (getBaseContext (), getResources ().getString (R.string.Press_back_again), Toast.LENGTH_SHORT);
            backToast.show ();
        }
        backPressedTime = System.currentTimeMillis ();
    }
}