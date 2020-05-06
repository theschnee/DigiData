package edu.cs.dartmouth.myruns.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

import edu.cs.dartmouth.myruns.Models.HistoryModel;
import edu.cs.dartmouth.myruns.Models.ManualEntryModel;
import edu.cs.dartmouth.myruns.R;

import static edu.cs.dartmouth.myruns.Utils.SettingsActivity.UNITS_CHOSEN;

public class HistoryItemAdapter extends ArrayAdapter<HistoryModel> {

    private ArrayList<HistoryModel> items;
    private Context context;

    public HistoryItemAdapter(@NonNull Context context, ArrayList<HistoryModel> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HistoryModel entry = items.get(position);

        Double distance_num;
        Double distance_num2;
        String combined_data;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String receivedunits = preferences.getString(UNITS_CHOSEN, " ");

        if(!entry.getData().contains(receivedunits)){
            if(entry.getData().contains("Kilometers")){
                String[] parts = entry.getData().split(" ", 2);
                distance_num = Double.parseDouble(parts[0]);
                distance_num2 = distance_num*.62137;
                String new_unit = parts[1];
                combined_data = String.valueOf(distance_num2) + " " + receivedunits  + ", " + entry.getDuration();

            }
            else if(entry.getData().contains("Miles")){
                String[] parts = entry.getData().split(" ", 2);
                distance_num = Double.parseDouble(parts[0]);
                distance_num2 = distance_num*1.60934;
                String new_unit = parts[1];
                combined_data = String.valueOf(distance_num2) + " " + receivedunits  + ", " + entry.getDuration();
            }


            else{
                combined_data = entry.getData() + ", " + entry.getDuration();
            }

        }else{
            combined_data = entry.getData() + ", " + entry.getDuration();

        }


        convertView = LayoutInflater.from(context).inflate(R.layout.display_entry, parent, false);

        String combinedleftitle = entry.getEntryType() + ": " + entry.getTitle();
     //   String combined_data = String.valueOf(distance_num2) + receivedunits  + ", " + entry.getDuration();

        TextView left_title = convertView.findViewById(R.id.display_title_left);
        TextView date = convertView.findViewById(R.id.display_title_right);
        TextView right_title = convertView.findViewById(R.id.display_summary);

        left_title.setText(combinedleftitle);
        date.setText(entry.getDate());
        right_title.setText(combined_data);

        //    return super.getView(position, convertView, parent);
        return convertView;
    }

}
