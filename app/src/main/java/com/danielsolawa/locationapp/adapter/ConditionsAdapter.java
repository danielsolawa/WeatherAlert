package com.danielsolawa.locationapp.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.model.Alert;

import java.util.List;

/**
 * Created by NeverForgive on 2017-09-08.
 */

public class ConditionsAdapter extends ArrayAdapter<Alert>{

    private static final int ROW_LAYOUT = R.layout.conditions_item;
    private Context context;
    private List<Alert> alerts;
    private RowClicker clicker;

    public ConditionsAdapter(@NonNull Context context,@NonNull List<Alert> alerts, RowClicker clicker) {
        super(context, ROW_LAYOUT, alerts);
        this.context = context;
        this.alerts = alerts;
        this.clicker = clicker;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Alert alert = alerts.get(position);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(ROW_LAYOUT, parent, false);
        TextView localityTv = (TextView) rowView.findViewById(R.id.locality_tv_lv);
        TextView tempTv = (TextView) rowView.findViewById(R.id.temperature_tv_lv);
        TextView descTv = (TextView) rowView.findViewById(R.id.desc_tv_lv);

        localityTv.setText(alert.getLocality().getName());
        tempTv.setText(alert.getTemperatureCondition() + "\u00b0C");
        descTv.setText(alert.getWeatherCondition());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicker.onOnlick(alert.getId());
            }
        });


        return rowView;
    }
}
