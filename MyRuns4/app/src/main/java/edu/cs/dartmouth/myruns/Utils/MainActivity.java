package edu.cs.dartmouth.myruns.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.cs.dartmouth.myruns.Fragments.HistoryFragment;
import edu.cs.dartmouth.myruns.R;
import edu.cs.dartmouth.myruns.Fragments.StartFragment;

public class MainActivity extends AppCompatActivity {
    public static final String IS_SIGNED_IN = "signed in";
    private BottomNavigationView mNavigation;
    private boolean mLoggedIn = false;
    private boolean mProfileChange = false;
    private int mFragmentSelect;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mLoggedIn = getIntent().getBooleanExtra(SignInActivity.CLICKING_SIGINING_IN, false);
        mProfileChange = getIntent().getBooleanExtra(ProfileActivity.PROFILE_CHANGE, false);
        mFragmentSelect = getIntent().getIntExtra(ManualEntryActivity.FRAGMENT_NUM, 0);

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if(mFragmentSelect == 0) {
                actionBar.setTitle("Start Activity");
            } else {
                actionBar.setTitle("History");
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(mFragmentSelect);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    actionBar.setTitle("Start Activity");
                    mNavigation.getMenu().findItem(R.id.begin_workout).setChecked(true);
                } else if(position == 1) {
                    actionBar.setTitle("History");
                    mNavigation.getMenu().findItem(R.id.history).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNavigation = findViewById(R.id.bottomNavigationView);
        mNavigation.setFocusableInTouchMode(true);
        if(mFragmentSelect == 0) {
            mNavigation.setSelectedItemId(R.id.begin_workout);
        } else {
            mNavigation.setSelectedItemId(R.id.history);
        }
        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.begin_workout:
                        actionBar.setTitle("Start Activity");
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.history:
                        actionBar.setTitle("History");
                        mViewPager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.edit_profile:
                Intent profile = new Intent( MainActivity.this, ProfileActivity.class);
                profile.putExtra(IS_SIGNED_IN, true);
                startActivity(profile);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StartFragment();
                case 1:
                    return new HistoryFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
