package edu.cs.dartmouth.myruns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.cs.dartmouth.myruns.Fragments.HistoryFragment;
import edu.cs.dartmouth.myruns.Fragments.StartFragment;
import edu.cs.dartmouth.myruns.Models.ExerciseEntry;
import edu.cs.dartmouth.myruns.Utils.MainActivity;

import static edu.cs.dartmouth.myruns.MyIntentService.current_location;
import static edu.cs.dartmouth.myruns.Utils.ManualEntryActivity.FRAGMENT_NUM;
import static edu.cs.dartmouth.myruns.Utils.SettingsActivity.UNITS_CHOSEN;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQ_CODE = 1 ;
    public static final String IN_FOREGROUND = "In_foreground";
    public static boolean isInForeGround = true;

    private GoogleMap mMap;
    private Marker mMarker;
    private Intent serviceIntent;
    LatLng location_current_position;

    SupportMapFragment mapFragment;
    private AsyncTaskHelper task;
    private boolean isView;
    private String eyeD;

    // All fields that calculate values for speed, movement, etc. needed by exerciseEntry
    private String inputType;
    private String activityType;
    private double mAvgSpeed;
    private double mCalorie;
    private double mClimbed;
    private double mDistance;
    private double mDuration;
    private double mSpeed;
    private double mStartAltitude;
    private Date mStartTime;
    private ArrayList<LatLng> locations = new ArrayList<>();

    private SimpleDateFormat mCurrentDate;
    private SimpleDateFormat mTime;

    // all textViews for display
    private TextView activityView;
    private TextView speedView;
    private TextView climbView;
    private TextView calorieView;
    private TextView distanceView;

    // get unit preference
    private String receivedunits;

    // for polylines
    Polyline polyline;
    PolylineOptions recOptions;

    /* @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO Save the state of the program
    } */ // TODO NOT SURE WE NEED THIS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        inputType = getIntent().getStringExtra(StartFragment.Type_of_Input);
        isView = getIntent().getBooleanExtra(HistoryFragment.DELETE, false);
        eyeD = getIntent().getStringExtra(HistoryFragment.ID);

        // get supportActionBar and start it up
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // get unit preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        receivedunits = preferences.getString(UNITS_CHOSEN, "Miles");

        /* if (savedInstanceState != null) {
            // TODO get stored data on instance change
        } else {
            // TODO initialize relevant variables
        } */

        // Obtain all the textViews
        activityView = findViewById(R.id.Activity);
        speedView = findViewById(R.id.speed);
        climbView = findViewById(R.id.climbed);
        calorieView = findViewById(R.id.Calorie);
        distanceView = findViewById(R.id.distance);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // check whether permissions are granted
        if(!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ_CODE);
        } else if (!isView) {
            StartTrackingService();
        }

        // check if the activity should be view only or not
        if(!isView) {
            // get current date and time
            mCurrentDate = new SimpleDateFormat("MM/dd/YYYY", Locale.getDefault());
            mTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mStartTime = Calendar.getInstance().getTime();

            // open broadcast receiver channel
            LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBroadcastReciever, new IntentFilter(LocationUpdate.BROADCAST_LOCATION));

            // open channel for activity detection if automatic
            if(inputType.contentEquals("Automatic")) {
                LocalBroadcastManager.getInstance(this).registerReceiver(anotherLocationBroadcastReceiver, new IntentFilter(ActivityRecognition.BROADCAST_DETECTED_ACTIVITY));
            } else {
                activityType = getIntent().getStringExtra(StartFragment.Type_of_Activity);
            }
        } else {
            // get datasource and initialize exerciseActivity if called from history activity
            DataSource dataSource = new DataSource(this);
            dataSource.open();
            ExerciseEntry entry = dataSource.getExerciseEntry(eyeD);
            reinitializefields(entry);
        }
    }

    /**
     * Methods for the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isView) {
            getMenuInflater().inflate(R.menu.menu_display_entry, menu);
            ActionBar bar = getSupportActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle("Map");
        } else {
            getMenuInflater().inflate(R.menu.menu_manual_entry, menu);
            ActionBar bar = getSupportActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle("Map");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                if (!isView) {
                    System.out.println("HOME");
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcastReciever);
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(anotherLocationBroadcastReceiver);
                    stopService(serviceIntent);
                    Intent goBack = new Intent(MapsActivity.this, MainActivity.class);
                    startActivity(goBack);
                } else {
                    Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                    intent.putExtra(FRAGMENT_NUM, 1);
                    startActivity(intent);
                }
                break;
            }
            case R.id.action_save: {
                if (!isView) {
                    System.out.println("SAVING");
                    SaveEntry();
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcastReciever);
                    LocalBroadcastManager.getInstance(this).unregisterReceiver(anotherLocationBroadcastReceiver);
                    stopService(serviceIntent);
                    Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }

            case R.id.action_delete: {
                task = new AsyncTaskHelper(isView, eyeD, this);
                task.execute();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                intent.putExtra(FRAGMENT_NUM, 1);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Methods that interact with google maps
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        LatLng locations = new LatLng(location_current_position.latitude, location_current_position.longitude);
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        mMarker = mMap.addMarker(new MarkerOptions().position(locations).title("My location at one point i think !"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location_current_position, 17));

        // TODO Draw map for everything if it is called from history

    }

    private void StartTrackingService() {
        serviceIntent = new Intent( this, MyIntentService.class);
        //   this.startService(serviceIntent);
        startForegroundService(serviceIntent);
    }

    BroadcastReceiver mLocationBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(LocationUpdate.BROADCAST_LOCATION)){
                Location location = intent.getParcelableExtra(current_location);

                if (location != null) {
                    location_current_position = new LatLng(location.getLatitude(),location.getLongitude());


                    if (mMarker != null) {
                        if (polyline == null) {
                            recOptions = new PolylineOptions().add(mMarker.getPosition());
                            recOptions.add(mMarker.getPosition());
                            recOptions.color(Color.GREEN);
                            polyline = mMap.addPolyline(recOptions);
                            mMarker.remove();
                        } else {
                            recOptions.add(mMarker.getPosition());
                            recOptions.color(Color.GREEN);
                            polyline = mMap.addPolyline(recOptions);
                            mMarker.remove();
                        }
                    }

                    mMarker = mMap.addMarker(new MarkerOptions().position(location_current_position).title("Current Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location_current_position, 17));
                }

                // check if this location is the first one
                if(locations.isEmpty()) {
                    // set starting altitude
                    mStartAltitude = location.getAltitude();
                }

                // add latlng to array of latlngs
                locations.add(location_current_position);

                // calculate speed (CORRECT)
                mSpeed = location.getSpeed();

                // calculate climb (CORRECT)
                mClimbed = (location.getAltitude() - mStartAltitude);

                // calculate distance (in kms, CORRECT)
                if(locations.size() >= 2) {
                    LatLng latLng2 = locations.get(locations.size()-1);
                    LatLng latLng1 = locations.get(locations.size()-2);

                    double theta = latLng1.longitude - latLng2.longitude;
                    double dist = Math.sin(latLng1.latitude * Math.PI / 180.0)
                            * Math.sin(latLng2.latitude * Math.PI / 180.0)
                            + Math.cos(latLng1.latitude * Math.PI / 180.0)
                            * Math.cos(latLng2.latitude * Math.PI / 180.0)
                            * Math.cos(theta * Math.PI / 180.0);
                    dist = Math.acos(dist);
                    dist = dist * 180.0 / Math.PI;
                    dist = dist * 60 * 1.1515;
                    mDistance = mDistance + dist;
                } else {
                    mDistance = 0;
                }

                // calculate calories (CALCULATION CORRECT, VALUE WRONG)
                mCalorie = mDistance * 0.06 * 1000;

                // Decimal format for clean display of values
                DecimalFormat df = new DecimalFormat("###0.00");

                // set textview of activityType if GPS
                if(inputType.equals("GPS")) {
                    activityView.setText("Activity: " + activityType);
                }

                // check what the preferred unit settings are
                if(receivedunits == "Miles") {

                    // Unit conversion from Metric to Imperial
                    double convertSpeed = mSpeed * 2.237; // meters per second to miles per hour
                    double convertClimbed = mClimbed / 0.3048; // meters to feet
                    double convertDistance = mDistance * 0.6214; // kilometers to miles

                    // set Textview texts
                    speedView.setText("Speed: " + df.format(convertSpeed) + " mph");
                    climbView.setText("Climb: " + df.format(convertClimbed) + " feet");
                    calorieView.setText("Calories: " + df.format(mCalorie) + " cals");
                    distanceView.setText("Distance: " + df.format(convertDistance) + " miles");

                } else {

                    // set Textview texts
                    speedView.setText("Speed: " + df.format(mSpeed) + " m/s");
                    climbView.setText("Climb: " + df.format(mClimbed) + " meters");
                    calorieView.setText("Calories: " + df.format(mCalorie) + " cals");
                    distanceView.setText("Distance: " + df.format(mDistance*1000) + " meters");
                }

            }
        }
    };

    BroadcastReceiver anotherLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ActivityRecognition.BROADCAST_DETECTED_ACTIVITY)){
                int type = intent.getIntExtra("type", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                handleUserActivity(type, confidence);
            }
        }
    };

    private void handleUserActivity(int type, int confidence) {
        String label = "Unknown";
        switch(type){
            case DetectedActivity.IN_VEHICLE: {
                label = "In a Vehicle";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "ON BICYCLE";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "ON FOOT";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "Running";
                break;
            }
            case DetectedActivity.STILL: {
                label = "Still";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "Tilting";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "Walking";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                break;
            }

        }

        activityType = label;
        activityView.setText("Activity: " + activityType);
    }

    /**
     * onPause and onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        isInForeGround = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeGround = false;
    }

    /**
     * Methods for handling data at different points in the app's cycle
     */
    private void SaveEntry() {
        boolean isInserted = true;
        ArrayList<String> strings = new ArrayList<>();

        // TODO: add to strings all necessary values for MapsActivity, figure out a way to pass LatLngs

        // add strings for inputType, activityType, date, time, duration
        strings.add(inputType);
        strings.add(activityType);
        //String date = mCurrentDate.format(new Date());
        strings.add(mCurrentDate.format(new Date()));
        strings.add(mTime.format(new Date()));

        // add string for duration
        Date currTime = Calendar.getInstance().getTime();
        long longDur = currTime.getTime() - mStartTime.getTime();
        long seconds = longDur/1000 % 60;
        long minutes = longDur/(60 * 1000) % 60;
        mDuration = (double) minutes + (double) seconds/60;
        strings.add((double) Math.round(mDuration*100)/100 + " mins");

        // add distance, calories, speed, average speed, altitude change
        strings.add(mDistance + " Kilometers");
        strings.add(String.valueOf(mCalorie));
        strings.add(String.valueOf(mSpeed));
        mAvgSpeed = mDistance/((double) longDur/1000);
        strings.add(String.valueOf(mAvgSpeed));
        strings.add(String.valueOf(mClimbed));

        task = new AsyncTaskHelper(isInserted, strings, this);
        task.execute();
    }

    private void reinitializefields(ExerciseEntry entry) {

        activityType = entry.getmActivityType();
        mSpeed = Double.parseDouble(entry.getmSpeed());
        mClimbed = Double.parseDouble(entry.getmClimb());
        mAvgSpeed = Double.parseDouble(entry.getmAvgSpeed());
        mCalorie = Double.parseDouble(entry.getmCalorie());
        String[] parts = entry.getmDistance().split(" ", 2);
        mDistance = Double.parseDouble(parts[0]);

        // Decimal format for clean display of values
        DecimalFormat df = new DecimalFormat("###0.00");

        if(receivedunits == "Miles") {

            // Unit conversion from Metric to Imperial
            double convertSpeed = mSpeed * 2.237; // meters per second to miles per hour
            double convertClimbed = mClimbed / 0.3048; // meters to feet
            double convertDistance = mDistance * 0.6214; // kilometers to miles

            // set Textview texts
            activityView.setText("Activity: " + activityType);
            speedView.setText(df.format(convertSpeed) + " mph");
            climbView.setText(df.format(convertClimbed) + " feet");
            calorieView.setText(df.format(mCalorie) + " cals");
            distanceView.setText(df.format(convertDistance) + " miles");
        } else {

            // set Textview texts
            activityView.setText("Activity: " + activityType);
            speedView.setText(df.format(mSpeed) + " m/s");
            climbView.setText(df.format(mClimbed) + " meters");
            calorieView.setText(df.format(mCalorie) + " cals");
            distanceView.setText(df.format(mDistance) + " kilometers");
        }

        // TODO Draw polylines/route on map

    };

    /**
     * Methods for permissions
     */
    private boolean checkPermission(){
        int permission_granted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission_granted == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            StartTrackingService();
        }else{
            finish();
        }

    }

    private class AsyncTaskHelper extends AsyncTask<Void, Void, Void> {

        public DataSource dataSource;
        private ArrayList<String> strings = new ArrayList<>();
        private String id;
        private boolean isView = false;
        private boolean isInserted;


        public AsyncTaskHelper(boolean isInserted, ArrayList<String> strings, Context context) {
            dataSource = new DataSource(context);
            this.strings = strings;
            this.isInserted = isInserted;
            //System.out.println("ASYNC SAVE:" + isInserted);
        }

        public AsyncTaskHelper(boolean isView, String id, Context context){
            dataSource = new DataSource(context);
            this.id = id;
            this.isView = isView;
            //System.out.println("ASYNC DELETE:" + isView);
        }

        @Override
        protected void onPreExecute() {
            dataSource.open();
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(isView) {
                //System.out.println(eyeD);
                dataSource.deleteExerciseEntry(id);
            }

            if(isInserted && !isView) {
                String date_time = strings.get(2) + " " + strings.get(3);
                dataSource.createMapExerciseEntry(strings.get(0), strings.get(1), date_time, strings.get(4), strings.get(5), strings.get(6), strings.get(7), strings.get(8), strings.get(9), locations);
            }
            isView = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dataSource.close();
        }
    }
}
