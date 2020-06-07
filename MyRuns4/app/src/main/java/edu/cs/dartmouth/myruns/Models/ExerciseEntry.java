package edu.cs.dartmouth.myruns.Models;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ExerciseEntry {

    /** All fields necessary for ManualEntryActivity
     *
     */
    private String id;
    private String mInputType;        // Manual, GPS or automatic
    private String mActivityType;     // Running, cycling etc.
    private String mDateTime;    // When does this entry happen
    private String mDuration;         // Exercise duration in seconds
    private String mDistance;      // Distance traveled. Either in meters or feet.
    private String mCalorie;          // Calories burnt
    private String mHeartRate;        // Heart rate
    private String mComment;       // Comments

    /** All additional fields necessary for MapsActivity entries
     *
     */
    private ArrayList<LatLng> mLocationList; // Location list
    private String mAvgSpeed;      // Average speed
    private String mClimb;         // Climb. Either in meters or feet.
    private String mSpeed;         // Current Speed? not sure why this is necessary

    public ExerciseEntry(){

    }

    /** All getters/setters necessary for ManualEntryActivity
     *
     */
    public String getid(){
        return id;
    }

    public void setid(String id){
        this.id = id;
    }

    public String getmInputType(){
        return mInputType;
    }

    public void setmInputType(String mInputType){
        this.mInputType = mInputType;
    }

    public String getmActivityType(){
        return mActivityType;
    }

    public void setmActivityType(String mActivityType){
        this.mActivityType = mActivityType;
    }

    public String getmDateTime(){
        return mDateTime;
    }

    public void setmDateTime(String mDateTime){
        this.mDateTime = mDateTime;
    }

    public String getmDuration(){
        return mDuration;
    }
    public void setmDuration(String mDuration){
        this.mDuration = mDuration;
    }

    public String getmDistance(){
        return mDistance;
    }
    public void setmDistance(String mDistance){
        this.mDistance = mDistance;
    }

    public String getmCalorie(){
        return mCalorie;
    }
    public void setmCalorie(String mCalorie){
        this.mCalorie = mCalorie;
    }

    public String getmHeartRate(){
        return mHeartRate;
    }
    public void setmHeartRate(String mHeartRate){
        this.mHeartRate = mHeartRate;
    }

    public String getmComment(){
        return mComment;
    }
    public void setmComment(String mComment){
        this.mComment = mComment;
    }

    /** All additional getters/setters necessary for MapsActivity
     *
     */
    public String getmAvgSpeed(){
        return mAvgSpeed;
    }
    public void setmAvgSpeed(String mAvgSpeed){
        this.mAvgSpeed = mAvgSpeed;
    }

    public String getmClimb() {
        return mClimb;
    }
    public void setmClimb(String mClimb){
        this.mClimb = mClimb;
    }

    public String getmSpeed() {
        return mSpeed;
    }
    public void setmSpeed(String mSpeed) {
        this.mSpeed = mSpeed;
    }

    public LatLng getPos(int idx) {
        return mLocationList.get(idx);
    }
    public void addPos(LatLng pos) {
        mLocationList.add(pos);
    }

    public ArrayList<LatLng> getAllPos() {
        return mLocationList;
    }

}
