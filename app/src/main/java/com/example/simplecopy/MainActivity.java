package com.example.simplecopy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.simplecopy.adapters.viewPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = MainActivity.class.getSimpleName();

    TabLayout tabLayout;
    AppBarLayout appBarLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    private FloatingActionButton fab;

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
                                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
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
        adapter.AddFragment (new CopyNoteList (), getString(R.string.notes));
        adapter.AddFragment (new CopyNumberList (), getResources ().getString (R.string.Numbers));
        adapter.AddFragment (new Daily_Wallet (), getResources ().getString (R.string.Daily_Wallet));

        viewPager.setAdapter (adapter);
    }

}