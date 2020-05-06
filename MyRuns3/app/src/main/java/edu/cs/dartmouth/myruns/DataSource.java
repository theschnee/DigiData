package edu.cs.dartmouth.myruns;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import edu.cs.dartmouth.myruns.Models.ExerciseEntry;

import static edu.cs.dartmouth.myruns.MySQLiteHelper.TABLE_EXERCISE;

public class DataSource {

    private MySQLiteHelper dbHelper;
    private SQLiteDatabase database;
    private String[] allColumns = new String[]{MySQLiteHelper.KEY_ROWID,
            MySQLiteHelper.KEY_INPUT_TYPE, MySQLiteHelper.KEY_ACTIVITY_TYPE, MySQLiteHelper.KEY_DATE_TIME, MySQLiteHelper.KEY_DURATION,
            MySQLiteHelper.KEY_DISTANCE, MySQLiteHelper.KEY_CALORIES, MySQLiteHelper.KEY_HEARTRATE, MySQLiteHelper.KEY_COMMENT};
    // private ArrayList<ExerciseEntry> exerciseEntries;

    public DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        // exerciseEntries = new ArrayList<>();
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
        dbHelper.close();
    }

    public void createExerciseEntry(String input_type, String activity_type, String date_time, String duration,
                                    String distance, String calories, String heartrate, String comment) {
        String placeholder = String.valueOf(0);
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.KEY_INPUT_TYPE, input_type);
        values.put(MySQLiteHelper.KEY_ACTIVITY_TYPE, activity_type);
        values.put(MySQLiteHelper.KEY_DATE_TIME, date_time);
        values.put(MySQLiteHelper.KEY_DURATION, duration);
        values.put(MySQLiteHelper.KEY_DISTANCE, distance);
        values.put(MySQLiteHelper.KEY_CALORIES, calories);
        values.put(MySQLiteHelper.KEY_HEARTRATE, heartrate);
        values.put(MySQLiteHelper.KEY_COMMENT, comment);
        values.put(MySQLiteHelper.KEY_ROWID, placeholder);

        long id = database.insert(TABLE_EXERCISE, null, values);
        String insertId = String.valueOf(id);

        ContentValues idValue = new ContentValues();
        idValue.put(MySQLiteHelper.KEY_ROWID, insertId);

        database.update(TABLE_EXERCISE, idValue, MySQLiteHelper.KEY_ROWID + "=?", new String[]{String.valueOf(0)} );

        System.out.println(MySQLiteHelper.KEY_INPUT_TYPE);

        Cursor cursor = database.query(TABLE_EXERCISE, new String[]{MySQLiteHelper.KEY_ROWID,
                MySQLiteHelper.KEY_INPUT_TYPE, MySQLiteHelper.KEY_ACTIVITY_TYPE, MySQLiteHelper.KEY_DATE_TIME, MySQLiteHelper.KEY_DURATION,
                MySQLiteHelper.KEY_DISTANCE, MySQLiteHelper.KEY_CALORIES, MySQLiteHelper.KEY_HEARTRATE, MySQLiteHelper.KEY_COMMENT}, MySQLiteHelper.KEY_ROWID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        System.out.println(cursor.getCount());

        cursorToExerciseEntry(cursor);
        cursor.close();

    }

    public void deleteExerciseEntry(String id) {

      //  String id = exerciseEntry.getid();
        // exerciseEntries.remove(exerciseEntry);
        database.delete(TABLE_EXERCISE, MySQLiteHelper.KEY_ROWID + " = " + id, null);
    }

    public void deleteAllExerciseEntry() {
        // exerciseEntries.clear();
        database.delete(TABLE_EXERCISE, null, null);

    }

    public ArrayList<ExerciseEntry> getAllExerciseEntry() {

        ArrayList<ExerciseEntry> exerciseEntries = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXERCISE, allColumns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ExerciseEntry exerciseEntry = new ExerciseEntry();
                exerciseEntry.setid(cursor.getString(0));
                exerciseEntry.setmInputType(cursor.getString(1));
                exerciseEntry.setmActivityType(cursor.getString(2));
                exerciseEntry.setmDateTime(cursor.getString(3));
                exerciseEntry.setmDuration(cursor.getString(4));
                exerciseEntry.setmDistance(cursor.getString(5));
                exerciseEntry.setmCalorie(cursor.getString(6));
                exerciseEntry.setmHeartRate(cursor.getString(7));
                exerciseEntry.setmComment(cursor.getString(8));
                exerciseEntries.add(exerciseEntry);
            } while(cursor.moveToNext());
        }
        return exerciseEntries;
    }

    // version of cursorToExerciseEntry without return for saving data
    private void cursorToExerciseEntry(Cursor cursor) {
        ExerciseEntry exerciseEntry = new ExerciseEntry();
        cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()) {
            exerciseEntry.setid(cursor.getString(0));
            exerciseEntry.setmInputType(cursor.getString(1));
            exerciseEntry.setmActivityType(cursor.getString(2));
            exerciseEntry.setmDateTime(cursor.getString(3));
            exerciseEntry.setmDuration(cursor.getString(4));
            exerciseEntry.setmDistance(cursor.getString(5));
            exerciseEntry.setmCalorie(cursor.getString(6));
            exerciseEntry.setmHeartRate(cursor.getString(7));
            exerciseEntry.setmComment(cursor.getString(8));
        }
    }

    public ExerciseEntry getExerciseEntry(String id) {
        Cursor cursor = database.query(TABLE_EXERCISE, new String[]{MySQLiteHelper.KEY_ROWID,
                MySQLiteHelper.KEY_INPUT_TYPE, MySQLiteHelper.KEY_ACTIVITY_TYPE, MySQLiteHelper.KEY_DATE_TIME, MySQLiteHelper.KEY_DURATION,
                MySQLiteHelper.KEY_DISTANCE, MySQLiteHelper.KEY_CALORIES, MySQLiteHelper.KEY_HEARTRATE, MySQLiteHelper.KEY_COMMENT}, MySQLiteHelper.KEY_ROWID + " = " + id, null, null, null, null);
        ExerciseEntry exerciseEntry = new ExerciseEntry();
        cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()) {
            exerciseEntry.setid(cursor.getString(0));
            exerciseEntry.setmInputType(cursor.getString(1));
            exerciseEntry.setmActivityType(cursor.getString(2));
            exerciseEntry.setmDateTime(cursor.getString(3));
            exerciseEntry.setmDuration(cursor.getString(4));
            exerciseEntry.setmDistance(cursor.getString(5));
            exerciseEntry.setmCalorie(cursor.getString(6));
            exerciseEntry.setmHeartRate(cursor.getString(7));
            exerciseEntry.setmComment(cursor.getString(8));
        }

        cursor.close();
        return exerciseEntry;
    }
}