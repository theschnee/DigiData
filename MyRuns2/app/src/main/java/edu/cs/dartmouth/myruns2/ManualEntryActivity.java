package edu.cs.dartmouth.myruns2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ManualEntryActivity extends AppCompatActivity {

    private static final String SAVING_INFO = "saves info for rotation" ;
    private ListView mListView;
    private ManualInputAdapter mAdapter;
    private ArrayList<ManualEntryModel> mItems;
    public Calendar cal = Calendar.getInstance();

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

        if(savedInstanceState != null) {
            mItems = savedInstanceState.getParcelableArrayList(SAVING_INFO);
            mAdapter = new ManualInputAdapter(this, mItems);
            mListView.setAdapter(mAdapter);
        }
        else{
            mItems = new ArrayList<>();
            mAdapter = new ManualInputAdapter(this, mItems);
            mListView.setAdapter(mAdapter);
            intializefields();
        }

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle("Manual Input");

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Toast.makeText(getApplicationContext(), "position" + position, Toast.LENGTH_SHORT).show();
                if(position !=0){
                    switch(position){
                        case 1:
                            DatePickerDialog dayofchoice = new DatePickerDialog(ManualEntryActivity.this, DayPicker, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                            dayofchoice.setTitle("Please Select a Date");
                            dayofchoice.show();
                            break;
                        case 2:
                            TimePickerDialog timeofday = new TimePickerDialog(ManualEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    mItems.get(position).setData(hourOfDay + ":" + minute);
                                    mListView.setAdapter(mAdapter);

                                }
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                                timeofday.setTitle("Select a Time");
                                timeofday.show();
                        break;

                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            AlertDialog.Builder builder = new AlertDialog.Builder(ManualEntryActivity.this);
                            final EditText userinput = new EditText(ManualEntryActivity.this);
                            builder.setTitle("Input Data");
                            builder.setMessage("Please input:");
                            builder.setView(userinput);

                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String udatedvalue = userinput.getText().toString();
                                    if(position == 3) {
                                        mItems.get(position).setData(udatedvalue + " mins");
                                        mListView.setAdapter(mAdapter);
                                    }
                                    if(position == 4) {
                                        mItems.get(position).setData(udatedvalue + " kms");
                                        mListView.setAdapter(mAdapter);
                                    }
                                    if(position == 5) {
                                        mItems.get(position).setData(udatedvalue + " cals");
                                        mListView.setAdapter(mAdapter);
                                    }
                                    if(position == 6) {
                                        mItems.get(position).setData(udatedvalue + " bpm");
                                        mListView.setAdapter(mAdapter);
                                    }
                                    if(position == 7) {
                                        mItems.get(position).setData(udatedvalue);
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

    private void intializefields(){
        String activity_registered = getIntent().getStringExtra(StartFragment.Type_of_Activity);
        if(activity_registered != null)
        mItems.add(new ManualEntryModel("Activity", activity_registered));

        mItems.add(new ManualEntryModel("Date", "Today"));
        mItems.add(new ManualEntryModel("Time", "Now"));
        mItems.add(new ManualEntryModel("Duration", "0 mins"));
        mItems.add(new ManualEntryModel("Distance", "0 kms"));
        mItems.add(new ManualEntryModel("Calories", "0 cals"));
        mItems.add(new ManualEntryModel("Heartbeat", "0 bpm"));
        mItems.add(new ManualEntryModel("Comment", "None"));
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

    private void updatecalendar(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        mItems.get(1).setData(sdf.format(cal.getTime()));
        mListView.setAdapter(mAdapter);
    }

}
