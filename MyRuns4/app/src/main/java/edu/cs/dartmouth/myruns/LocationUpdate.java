package edu.cs.dartmouth.myruns;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import static edu.cs.dartmouth.myruns.MapsActivity.isInForeGround;


public class LocationUpdate extends IntentService {

    private static final String ACTION_FOO = "edu.cs.dartmouth.myruns.action.FOO";
    private static final String ACTION_BAZ = "edu.cs.dartmouth.myruns.action.BAZ";
    private static final String EXTRA_PARAM1 = "edu.cs.dartmouth.myruns.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "edu.cs.dartmouth.myruns.extra.PARAM2";

    public static final String BROADCAST_LOCATION = "Broadcast updated location";
    public static final String LOCATION_UPDATE = "Location Updated";
    public static final String current_location = "location";
    private static ArrayList<Location> locBuffer;
//    long interval = 5000;
//    long upperbound = 1000;

    public LocationUpdate() {
        super("LocationUpdate");
    }



    @Override
    public void onCreate() {
        super.onCreate();
        if(locBuffer == null) {
            locBuffer = new ArrayList<>();
        }
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

//        if(locBuffer == null) {
//            locBuffer = new ArrayList<>();
//        }

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            if(locBuffer == null) {
                locBuffer = new ArrayList<>();
                Location location = intent.getParcelableExtra(current_location);
                locBuffer.add(location);
            }else{
                Location location = intent.getParcelableExtra(current_location);
                locBuffer.add(location);
            }

            if(isInForeGround) {
                broadcastLocation();
            }
        }
    }
    private void broadcastLocation(){
        for(int i=0; i < locBuffer.size(); i++){
            Location loc = locBuffer.get(i);
            Intent intent = new Intent(BROADCAST_LOCATION);
            intent.putExtra(current_location, loc);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        locBuffer.clear();
    }

}
