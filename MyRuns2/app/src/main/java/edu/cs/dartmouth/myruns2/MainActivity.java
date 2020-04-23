package edu.cs.dartmouth.myruns2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String IS_SIGNED_IN = "signed in";
    private BottomNavigationView mNavigation;
    private boolean mLoggedIn = false;
    private boolean mProfileChange = false;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoggedIn = getIntent().getBooleanExtra(SignInActivity.CLICKING_SIGINING_IN, false);
        mProfileChange = getIntent().getBooleanExtra(ProfileActivity.PROFILE_CHANGE, false);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        mNavigation = findViewById(R.id.bottomNavigationView);
        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.begin_workout:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.history:
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
