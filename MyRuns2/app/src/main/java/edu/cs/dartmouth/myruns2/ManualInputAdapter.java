package edu.cs.dartmouth.myruns2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.cs.dartmouth.myruns2.ManualEntryModel;

public class ManualInputAdapter extends ArrayAdapter<ManualEntryModel> {

    private ArrayList<ManualEntryModel> items;
    private Context context;


    public ManualInputAdapter(@NonNull Context context, ArrayList<ManualEntryModel> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ManualEntryModel entry = items.get(position);
        convertView = LayoutInflater.from(context).inflate(R.layout.manual_item_entry, parent, false);
        TextView title = convertView.findViewById(R.id.text_title);
        TextView data = convertView.findViewById(R.id.text_data);
        title.setText(entry.getTitle());
        data.setText(entry.getData());

    //    return super.getView(position, convertView, parent);
        return convertView;


    }
}
