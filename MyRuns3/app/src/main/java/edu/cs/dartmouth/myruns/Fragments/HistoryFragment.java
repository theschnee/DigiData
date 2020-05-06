package edu.cs.dartmouth.myruns.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.cs.dartmouth.myruns.Adapters.HistoryItemAdapter;
import edu.cs.dartmouth.myruns.Adapters.ManualInputAdapter;
import edu.cs.dartmouth.myruns.DataSource;
import edu.cs.dartmouth.myruns.ExerciseEntriesListLoader;
import edu.cs.dartmouth.myruns.Models.ExerciseEntry;
import edu.cs.dartmouth.myruns.Models.HistoryModel;
import edu.cs.dartmouth.myruns.Models.ManualEntryModel;
import edu.cs.dartmouth.myruns.R;
import edu.cs.dartmouth.myruns.Utils.ManualEntryActivity;

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ExerciseEntry>> {


    private static final int ALL_EXERCISE_ENTRY_LOADER_ID = 1;
    public static final String DELETE = "Delete";
    public static final String ID = "id";
    private DataSource datasource;
    private HistoryItemAdapter mAdapter;
    private ArrayList<HistoryModel> mItems;
    private ListView mListView;
    private TextView mLeftTextView;
    private TextView mRightTextView;
    private TextView mBottomTextView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLeftTextView = (TextView) getView().findViewById(R.id.display_title_left);
        mRightTextView = (TextView) getView().findViewById(R.id.display_title_right);
        mBottomTextView = (TextView) getView().findViewById(R.id.display_summary);
        mListView = (ListView) getView().findViewById(R.id.list_view_history);

        mItems = new ArrayList<>();
        mAdapter = new HistoryItemAdapter(getContext(), mItems);
        mListView.setAdapter(mAdapter);


        datasource = new DataSource(getContext());
        datasource.open();
        final ArrayList<ExerciseEntry> entries = datasource.getAllExerciseEntry();


        for(int i = 0; i < entries.size(); i++) {
            ExerciseEntry entry = entries.get(i);
            mItems.add(i, new HistoryModel(entry.getmActivityType(), entry.getmDistance(),
                    entry.getmDateTime(), entry.getmInputType(), entry.getmDuration(), entry.getid() ));
        }

        datasource.close();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "HELP I HOPE THIS WORKS" + position + mItems.get(position).id , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), ManualEntryActivity.class);
                intent.putExtra("Delete", true);
                intent.putExtra("id", mItems.get(position).getId());
                startActivity(intent);
            }
        });





    }


    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.action_delete:
                if (mAdapter.getCount() > 0) {
                    //exerciseEntry = mAdapter.getItem(0);
                    //datasource.deleteExerciseEntry(exerciseEntry);
                    //mAdapter.remove(exerciseEntry);
                }
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        datasource.open();
        super.onResume();

    }

    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }

    @NonNull
    @Override
    public Loader<List<ExerciseEntry>> onCreateLoader(int id, @Nullable Bundle args) {

        if(id == ALL_EXERCISE_ENTRY_LOADER_ID)
            return new ExerciseEntriesListLoader(getContext());

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ExerciseEntry>> loader, List<ExerciseEntry> data) {

        if(loader.getId() == ALL_EXERCISE_ENTRY_LOADER_ID) {
            if(data.size() > 0){
                //mAdapter.addAll(data);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ExerciseEntry>> loader) {

    }
}
