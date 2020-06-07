package edu.cs.dartmouth.myruns;

import android.app.IntentService;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import static edu.cs.dartmouth.myruns.MapsActivity.isInForeGround;


public class ActivityRecognition extends IntentService {

    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

    private static ArrayList<DetectedActivity> activityBuffer;



    public ActivityRecognition() {
        super("ActivityRecognition");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(activityBuffer == null) {
            activityBuffer = new ArrayList<>();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            if(activityBuffer == null) {
                activityBuffer = new ArrayList<>();
                activityBuffer.add(result.getMostProbableActivity());
            }else {
                activityBuffer.add(result.getMostProbableActivity());
            }

            if (isInForeGround){
                broadcastActivity();
            }

        }
    }
    private void broadcastActivity(){
        for(int i=0; i < activityBuffer.size(); i++){
            DetectedActivity activity = activityBuffer.get(i);
            Intent intent = new Intent(BROADCAST_DETECTED_ACTIVITY);
            intent.putExtra("type", activity.getType());
            intent.putExtra("confidence", activity.getConfidence());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        activityBuffer.clear();
    }
}
