package edu.cs.dartmouth.myruns.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import edu.cs.dartmouth.myruns.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String SETTINGS_CHANGE = "settings change";
    public static final String UNITS_CHOSEN = "The units that were chosen" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    /* onClickListener to return home properly without going back to SignInActivity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent profileChange = new Intent(SettingsActivity.this, MainActivity.class);
                profileChange.putExtra(SETTINGS_CHANGE, true);
                startActivity(profileChange);
        }
        return super.onOptionsItemSelected(item);
    }

    /* EVERYTHING TO DO WITH THE SETTINGS FRAGMENT */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            /* getPreferenceManager().setOnPreferenceTreeClickListener(new PreferenceManager.OnPreferenceTreeClickListener() {
                @Override
                public boolean onPreferenceTreeClick(Preference preference) {
                    switch (preference.getKey()) {
                        case "sign_out_settings_button":
                            Intent signOut = new Intent(getContext(), SignInActivity.class);
                            signOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().finish();
                            startActivity(signOut);
                            return true;
                        case "privacy":
                            return true;

                    }
                    return false;
                }
            }); */



            Preference mSignOut =  getPreferenceManager().findPreference("sign_out_settings_button");
            mSignOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent signOut = new Intent(getContext(), SignInActivity.class);
                    signOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getActivity().finish();
                    startActivity(signOut);
                    return true;
                }
            });


            final ListPreference mUnitTank = (ListPreference) findPreference("unit");

            mUnitTank.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mUnitTank.setValue(newValue.toString());

                    preference.setSummary(mUnitTank.getEntry());

                    if(mUnitTank.getEntry().toString().equals("Miles")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(UNITS_CHOSEN, "Miles");
                        editor.commit();
                    } else {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(UNITS_CHOSEN, "Kilometers");
                        editor.commit();
                    }
                    return false;
                }
            });
        }

    }
}