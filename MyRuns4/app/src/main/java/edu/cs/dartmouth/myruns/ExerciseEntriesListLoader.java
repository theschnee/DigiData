package edu.cs.dartmouth.myruns;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

import edu.cs.dartmouth.myruns.Models.ExerciseEntry;

public class ExerciseEntriesListLoader extends AsyncTaskLoader<List<ExerciseEntry>> {

    private final DataSource dataSource;



    public ExerciseEntriesListLoader(@NonNull Context context) {
        super(context);
        dataSource = new DataSource(context);
        dataSource.open();
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    @Nullable
    @Override
    public List<ExerciseEntry> loadInBackground() {
        return dataSource.getAllExerciseEntry();
    }








}
