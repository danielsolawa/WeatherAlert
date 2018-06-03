package com.danielsolawa.locationapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.danielsolawa.locationapp.R;

import java.util.List;

/**
 * Created by NeverForgive on 2018-06-03.
 */

public class EmailAdapter extends ArrayAdapter<String> {
    private static final int ROW_LAYOUT = R.layout.email_item;
    private List<String> emails;
    private RowClicker clicker;
    private Context context;

    public EmailAdapter(@NonNull Context context, @NonNull List<String> emails, RowClicker clicker) {
        super(context, ROW_LAYOUT, emails);
        this.context = context;
        this.emails = emails;
        this.clicker = clicker;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String email = emails.get(position);

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(ROW_LAYOUT, parent, false);

        TextView emailTv = (TextView) rowView.findViewById(R.id.email_item_lv);
        emailTv.setText(email);


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicker.onClick(position, email);
            }
        });

        return rowView;
    }
}
