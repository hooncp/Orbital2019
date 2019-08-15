package com.example.islandmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.islandmark.model.LandmarkDetails;

import java.util.List;

public class LandmarkDetailsAdapter extends ArrayAdapter<LandmarkDetails> {
    private final Context context;
    private final List<LandmarkDetails> values;

    public LandmarkDetailsAdapter(Context context, List<LandmarkDetails> values) {
        super(context, R.layout.landmark_details_list_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.landmark_details_list_row, parent, false);

        TextView nameTV = rowView.findViewById(R.id.name);
        TextView descriptionTV = rowView.findViewById(R.id.description);
//        TextView locationTV = rowView.findViewById(R.id.location);

        if (MainActivity.language.equals("en")){
            nameTV.setText(values.get(position).name);
            descriptionTV.setText(values.get(position).description);
        } else {
            nameTV.setText(values.get(position).namezh);
            descriptionTV.setText("范例文字，请取代此段落文字。此段落文字为范例文字内容，请务必取代");
        }
//        locationTV.setText(values.get(position).distance +" m");
        return rowView;
    }

}
