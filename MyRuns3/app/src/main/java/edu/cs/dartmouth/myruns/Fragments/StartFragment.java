package edu.cs.dartmouth.myruns.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.cs.dartmouth.myruns.Adapters.ManualInputAdapter;
import edu.cs.dartmouth.myruns.R;
import edu.cs.dartmouth.myruns.Utils.ManualEntryActivity;

import static edu.cs.dartmouth.myruns.R.array;


public class StartFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    public static final String Type_of_Activity = "Activity Type";
    public static final String Type_of_Input = "Input Type";
    public static final String INTENT_FROM = "intent from";
    public static final String TAG = "StartFragment" ;
    private static final String CURRENT_ACTIVITY = "activity spinner";
    private static final String CURRENT_INPUT = "input spinner";

    private Spinner mInputSpinner;
    private Spinner mActivitySpinner;
    private String mCurrentActivity;
    private String mCurrentInput;
    private FloatingActionButton mBeginActivity;

    public StartFragment() {
        // Required empty public constructor
    }

    public static StartFragment newInstance(String param1, String param2) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(savedInstanceState != null) {
            mCurrentActivity = savedInstanceState.getString(CURRENT_ACTIVITY);
            mCurrentInput = savedInstanceState.getString(CURRENT_INPUT);
        }

        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mInputSpinner = view.findViewById(R.id.input_type);
        mActivitySpinner = view.findViewById(R.id.activity_type);
        mBeginActivity = view.findViewById(R.id.Start);

        ArrayAdapter<CharSequence> first = ArrayAdapter.createFromResource(getContext(), R.array.input_array, android.R.layout.simple_spinner_item);
        first.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInputSpinner.setAdapter(first);
        int posone = first.getPosition(mCurrentInput);
        mInputSpinner.setSelection(posone);
        mInputSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> second = ArrayAdapter.createFromResource(getContext(), array.activities_array, android.R.layout.simple_spinner_item);
        second.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mActivitySpinner.setAdapter(second);
        int postwo = second.getPosition(mCurrentActivity);
        mActivitySpinner.setSelection(postwo);
        mActivitySpinner.setOnItemSelectedListener(this);



        mBeginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInputSpinner.getSelectedItem().toString().equals("Manual")) {
                    Intent intent = new Intent(getContext(), ManualEntryActivity.class);
                    intent.putExtra(Type_of_Activity, mActivitySpinner.getSelectedItem().toString());
                    intent.putExtra(Type_of_Input, mInputSpinner.getSelectedItem().toString());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner first = (Spinner) parent;
        Spinner second = (Spinner) parent;
        String text = parent.getItemAtPosition(position).toString();
        if(first.getId() == R.id.input_type)
            mCurrentInput = text;
        if(mCurrentInput.equals("Automatic") || mCurrentInput.equals("GPS")) {
            mActivitySpinner.setEnabled(false);
        } else {
            mActivitySpinner.setEnabled(true);
        }
        if(second.getId() == R.id.activity_type)
            mCurrentActivity = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_ACTIVITY, mCurrentActivity);
        outState.putString(CURRENT_INPUT, mCurrentInput);
        super.onSaveInstanceState(outState);
    }
}
