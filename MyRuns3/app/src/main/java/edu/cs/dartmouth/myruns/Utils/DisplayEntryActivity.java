package edu.cs.dartmouth.myruns.Utils;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.cs.dartmouth.myruns.Adapters.ManualInputAdapter;
import edu.cs.dartmouth.myruns.Models.ManualEntryModel;
import edu.cs.dartmouth.myruns.R;

public class DisplayEntryActivity extends AppCompatActivity {

    private ListView mListView;
    private ManualInputAdapter mAdapter;
    private ArrayList<ManualEntryModel> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /* all methods for creating, modifying, and using the menu to sign up and go back,
     as necessary */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_entry, menu);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        // change this based on the title in the future
        bar.setTitle("Manual Entry Activity");


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                // insert method to delete item on history list here
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
