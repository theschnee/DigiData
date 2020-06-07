package edu.cs.dartmouth.myruns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

//import static android.media.MediaFormat.KEY_DURATION;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_EXERCISE = "exercise_table";
    private static final String DATABASE_NAME = "exercise.db";
    private static final int DATABASE_VERSION = 1;
    public static final String KEY_ROWID = "_id";
    public static final String KEY_INPUT_TYPE = "input_type";
    public static final String KEY_ACTIVITY_TYPE = "activity_type";
    public static final String KEY_DATE_TIME = "date_time";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_CALORIES = "calories";
    public static final String KEY_HEARTRATE = "heartrate";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_AVG_PACE = "average_pace";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_CLIMB = "altitude_change";

    public static final String CREATE_TABLE_ENTRIES = "Create Table If not Exists "
            + TABLE_EXERCISE
            + " ("
            + KEY_ROWID
            + " TEXT, "
            + KEY_INPUT_TYPE
            + " TEXT, "
            + KEY_ACTIVITY_TYPE
            + " TEXT, "
            + KEY_DATE_TIME
            + " TEXT, "
            + KEY_DURATION
            + " TEXT, "
            + KEY_DISTANCE
            + " TEXT, "
            + KEY_AVG_PACE
            + " TEXT, "
            + KEY_SPEED
            + " TEXT, "
            + KEY_CALORIES
            + " TEXT, "
            + KEY_CLIMB
            + " TEXT, "
            + KEY_HEARTRATE
            + " TEXT, "
            + KEY_COMMENT
            + " TEXT "
            //+ KEY_PRIVACY
            //+ " TEXT, "
            //+ KEY_GPS_DATA
            //+ " BLOB "
            + ");";


    public MySQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        onCreate(db);

    }
}
