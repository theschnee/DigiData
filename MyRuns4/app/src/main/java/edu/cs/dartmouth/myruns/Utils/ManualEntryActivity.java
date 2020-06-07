package edu.cs.dartmouth.myruns.Utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.cs.dartmouth.myruns.DataSource;
import edu.cs.dartmouth.myruns.Fragments.HistoryFragment;
import edu.cs.dartmouth.myruns.Models.ExerciseEntry;
import edu.cs.dartmouth.myruns.Utils.MainActivity;
import edu.cs.dartmouth.myruns.Models.ManualEntryModel;
import edu.cs.dartmouth.myruns.Adapters.ManualInputAdapter;
import edu.cs.dartmouth.myruns.R;
import edu.cs.dartmouth.myruns.Fragments.StartFragment;

import static edu.cs.dartmouth.myruns.Utils.SettingsActivity.UNITS_CHOSEN;

public class ManualEntryActivity extends AppCompatActivity {

    private static final String SAVING_INFO = "saves info for rotation";
    public static final String FRAGMENT_NUM = "History Fragment";
    private ListView mListView;
    private ManualInputAdapter mAdapter;
    private ArrayList<ManualEntryModel> mItems;
    public Calendar cal = Calendar.getInstance();
    private AsyncTaskHelper task;
    private boolean isInserted = false;
    private boolean isView;
    private String eyeD;



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVING_INFO, mItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        mListView = findViewById(R.id.list_view);

        isView = getIntent().getBooleanExtra(HistoryFragment.DELETE, false);
        eyeD = getIntent().getStringExtra(HistoryFragment.ID);

        DataSource dataSource = new DataSource(this);
        dataSource.open();
        ExerciseEntry entry = dataSource.getExerciseEntry(eyeD);

        System.out.println(eyeD);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mItems = savedInstanceState.getParcelableArrayList(SAVING_INFO);
            mAdapter = new ManualInputAdapter(this, mItems);
            mListView.setAdapter(mAdapter);
        } else {
            mItems = new ArrayList<>();
            mAdapter = new ManualInputAdapter(this, mItems);
            mListView.setAdapter(mAdapter);
            if(isView) reinitializefields(entry);
            if(!isView) intializefields();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String receivedunits = preferences.getString(UNITS_CHOSEN, " Miles");

        System.out.println(receivedunits);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle("Manual Input");

        if(!isView) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    Toast.makeText(getApplicationContext(), "position" + position, Toast.LENGTH_SHORT).show();
                    if (position != 0 && position != 1) {
                        switch (position) {
                            case 2:
                                DatePickerDialog dayofchoice = new DatePickerDialog(ManualEntryActivity.this, DayPicker, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                                dayofchoice.setTitle("Please Select a Date");
                                dayofchoice.show();
                                break;
                            case 3:
                                TimePickerDialog timeofday = new TimePickerDialog(ManualEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        if (minute < 10) {
                                            mItems.get(position).setData(hourOfDay + ":" + "0" + minute);
                                        } else {
                                            mItems.get(position).setData(hourOfDay + ":" + minute);

                                        }
                                        mListView.setAdapter(mAdapter);

                                    }
                                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                                timeofday.setTitle("Select a Time");
                                timeofday.show();
                                break;

                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ManualEntryActivity.this);
                                final EditText userinput = new EditText(ManualEntryActivity.this);
                                builder.setTitle("Input Data");
                                builder.setMessage("Please input:");

                                if(position!=8) {
                                    userinput.setRawInputType(Configuration.KEYBOARD_12KEY);
                                    userinput.setSingleLine(true);
                                }

                                builder.setView(userinput);

                                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String udatedvalue = userinput.getText().toString();

                                        if (position == 4) {
                                            if(userinput.getText().toString().contains(" ") || userinput.getText().toString().isEmpty()) {
                                                mItems.get(position).setData( "0" + " mins");
                                            } else {
                                                mItems.get(position).setData(udatedvalue + " mins");
                                            }
                                            mListView.setAdapter(mAdapter);
                                        }
                                        if (position == 5) {
                                            if(userinput.getText().toString().contains(" ") || userinput.getText().toString().isEmpty()) {
                                                mItems.get(position).setData( "0 " + receivedunits);
                                            } else {
                                                mItems.get(position).setData(udatedvalue + " " + receivedunits);
                                            }
                                            mListView.setAdapter(mAdapter);
                                        }
                                        if (position == 6) {
                                            if(userinput.getText().toString().contains(" ") || userinput.getText().toString().isEmpty()) {
                                                mItems.get(position).setData( "0" + " cals");
                                            } else {
                                                mItems.get(position).setData(udatedvalue + " cals");
                                            }
                                            mListView.setAdapter(mAdapter);
                                        }
                                        if (position == 7) {
                                            if(userinput.getText().toString().contains(" ") || userinput.getText().toString().isEmpty()) {
                                                mItems.get(position).setData( "0" + " bpm");
                                            } else {
                                                mItems.get(position).setData(udatedvalue + " bpm");
                                            }
                                            mListView.setAdapter(mAdapter);
                                        }
                                        if (position == 8) {
                                            if(userinput.getText().toString().isEmpty()) {
                                                mItems.get(position).setData("None");
                                            } else {
                                                mItems.get(position).setData(udatedvalue);
                                            }
                                            mListView.setAdapter(mAdapter);
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                                break;
                        }
                    }
                }
            });
        }

    }

    /* all methods for creating, modifying, and using the menu to sign up and go back,
    as necessary */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isView) {
            getMenuInflater().inflate(R.menu.menu_display_entry, menu);
            ActionBar bar = getSupportActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle("Manual Entry Activity");
        } else {
            getMenuInflater().inflate(R.menu.menu_manual_entry, menu);
            ActionBar bar = getSupportActionBar();
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle("Manual Entry Activity");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if(!isView) {
                    Intent goBack = new Intent(ManualEntryActivity.this, MainActivity.class);
                    startActivity(goBack);
                } else {
                    Intent intent = new Intent(ManualEntryActivity.this, MainActivity.class);
                    intent.putExtra(FRAGMENT_NUM, 1);
                    startActivity(intent);
                }

            case R.id.action_save:
                if (mAdapter.getCount() >= 0 && !isView) {
                    SaveEntry();
                    Intent intent = new Intent(ManualEntryActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;

            case R.id.action_delete:
                task = new AsyncTaskHelper(isView, eyeD, this);
                task.execute();
                Intent intent = new Intent(ManualEntryActivity.this, MainActivity.class);
                intent.putExtra(FRAGMENT_NUM, 1);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void SaveEntry() {
        isInserted = true;
        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < mItems.size(); i++) {
            strings.add(mItems.get(i).getData());
        }

        task = new AsyncTaskHelper(isInserted, strings, this);
        task.execute();
        isInserted = false;

    }

    private void intializefields() {
        String activity_registered = getIntent().getStringExtra(StartFragment.Type_of_Activity);

        Date currentTime = Calendar.getInstance().getTime();
        String now_time = String.valueOf(currentTime);
//        Time now = new Time(g);
        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String receivedunits = preferences.getString(UNITS_CHOSEN, "Miles");

        String input_registered = getIntent().getStringExtra(StartFragment.Type_of_Input);
        mItems.add(new ManualEntryModel("Activity Type", input_registered));

        if (activity_registered != null)
            mItems.add(new ManualEntryModel("Activity", activity_registered));

        mItems.add(new ManualEntryModel("Date", currentDate));
        mItems.add(new ManualEntryModel("Time", java.time.LocalTime.now().toString().substring(0,5)));
        mItems.add(new ManualEntryModel("Duration", "0 mins"));
        mItems.add(new ManualEntryModel("Distance", "0 " + receivedunits));
        mItems.add(new ManualEntryModel("Calories", "0 cals"));
        mItems.add(new ManualEntryModel("Heart Rate", "0 bpm"));
        mItems.add(new ManualEntryModel("Comment", "None"));
    }

    private void reinitializefields(ExerciseEntry entry) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String receivedunits = preferences.getString(UNITS_CHOSEN, "Miles");

        mItems.add(new ManualEntryModel("Activity Type", entry.getmInputType()));
        mItems.add(new ManualEntryModel("Activity", entry.getmActivityType()));

        String combo = entry.getmDateTime();
        String[] uncombo = combo.split(" ");
        mItems.add(new ManualEntryModel("Date", uncombo[0]));
        mItems.add(new ManualEntryModel("Time", uncombo[1]));
        mItems.add(new ManualEntryModel("Duration", entry.getmDuration()));

        String unit = entry.getmDistance();
        String[] numbers = unit.split(" ");
        Double distance_num2 = Double.parseDouble(numbers[0]);
        if(!entry.getmDistance().contains(receivedunits)) {
            if(entry.getmDistance().contains("Kilometers")){
                String[] parts = entry.getmDistance().split(" ", 2);
                Double distance_num = Double.parseDouble(parts[0]);
                distance_num2 = distance_num*.62137;

            } else if(entry.getmDistance().contains("Miles")) {
                String[] parts = entry.getmDistance().split(" ", 2);
                Double distance_num = Double.parseDouble(parts[0]);
                distance_num2 = distance_num*1.60934;
            }
        }

        mItems.add(new ManualEntryModel("Distance", distance_num2 + " " + receivedunits));
        mItems.add(new ManualEntryModel("Calories", entry.getmCalorie()));
        mItems.add(new ManualEntryModel("Heart Rate", entry.getmHeartRate()));
        mItems.add(new ManualEntryModel("Comment", entry.getmComment()));

    }

    DatePickerDialog.OnDateSetListener DayPicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updatecalendar();
        }
    };

    private void updatecalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        mItems.get(2).setData(sdf.format(cal.getTime()));
        mListView.setAdapter(mAdapter);
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
                System.out.println(eyeD);
                dataSource.deleteExerciseEntry(id);
            }

            if(isInserted && !isView) {
                String date_time = strings.get(2) + " " + strings.get(3);
                dataSource.createManualExerciseEntry(strings.get(0), strings.get(1), date_time, strings.get(4), strings.get(5), strings.get(6), strings.get(7), strings.get(8));
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