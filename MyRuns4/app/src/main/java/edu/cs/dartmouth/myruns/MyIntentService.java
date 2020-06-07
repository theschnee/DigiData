package edu.cs.dartmouth.myruns;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MyIntentService extends Service {

//    private static final String ACTION_FOO = "edu.cs.dartmouth.myruns.action.FOO";
//    private static final String ACTION_BAZ = "edu.cs.dartmouth.myruns.action.BAZ";
//    private static final String EXTRA_PARAM1 = "edu.cs.dartmouth.myruns.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "edu.cs.dartmouth.myruns.extra.PARAM2";
    public static final String LOCATION_UPDATE = "Location Updated";
    public static final String current_location = "location";
    public static final String LOC_TASK_ID = "LOC_TASK_ID";
    private static final int SERVICE_NOTFICATION_ID = 1;
    long interval = 5000;
    long upperbound = 1000;
    private NotificationManager notificationManager;
    private ActivityRecognitionClient mActivityRecognition;
    private PendingIntent mPendingIntent;
    public static final long DETECTION_INTERVAL_MS = 2000;


    public MyIntentService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startLocationUpdate();
        createNotification();

//        mActivityRecognition = new ActivityRecognitionClient(this);
//        Intent mIntent = new Intent(MyIntentService.this, ActivityRecognition.class);
//        mPendingIntent = PendingIntent.getService(this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        requestActivityUpdateHandler();
    }

    private void startLocationUpdate() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(upperbound);
        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Intent mIntentService = new Intent(MyIntentService.this, LocationUpdate.class);
            mIntentService.putExtra(current_location, locationResult.getLastLocation());
            //  mIntentService.putExtra(LOC_TASK_ID, locTaskId);
            startService(mIntentService);

//            Intent intent = new Intent(LOCATION_UPDATE);
//            intent.putExtra(current_location, locationResult.getLastLocation());
//            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }

//        @Override
//        public void onLocationAvailability(LocationAvailability locationAvailability) {
//            super.onLocationAvailability(locationAvailability);
//        }
    };

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mActivityRecognition = new ActivityRecognitionClient(this);
        Intent mIntent = new Intent(MyIntentService.this, ActivityRecognition.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdateHandler();

        return START_STICKY;
    }

    public void requestActivityUpdateHandler() {
        if(mActivityRecognition != null){
            Task<Void> task = mActivityRecognition.requestActivityUpdates(DETECTION_INTERVAL_MS, mPendingIntent);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivity();
    }

    public void removeActivity() {
        if(mActivityRecognition !=null){
            Task<Void> task = mActivityRecognition.removeActivityUpdates(mPendingIntent);
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void createNotification(){

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "tracking";
        String name = "MyRuns";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
            String description = "Chanel Description";

            channel.setDescription(description);
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new Notification.Builder(this, channelId )
                .setContentTitle("MyRuns")
                .setContentText("Your Location is being tracked")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(SERVICE_NOTFICATION_ID, notification);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
