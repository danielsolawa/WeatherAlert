package com.danielsolawa.locationapp.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.model.WeatherData;
import com.danielsolawa.locationapp.utils.Constants;
import com.danielsolawa.locationapp.utils.DateUtils;
import com.danielsolawa.locationapp.utils.Localization;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by NeverForgive on 2017-09-13.
 */

public class HorizontalListViewFragment extends Fragment {

    public static final String TAG = HorizontalListViewFragment.class.getSimpleName();
    private List<WeatherData> listData = new ArrayList<>();
    private RecyclerView recyclerView;
    private boolean darkMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parcelable list = getArguments().getParcelable(Constants.WEATHER_DATA_LIST);
        darkMode = getArguments().getBoolean(Constants.DARK_MODE);
        listData = Parcels.unwrap(list);




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if(listData.size() > 0 && recyclerView != null){
            recyclerView.setAdapter(new WeatherAdapter(listData));
        }
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public class WeatherAdapter extends  RecyclerView.Adapter<WeatherViewHolder>{
        private List<WeatherData> list;

        public WeatherAdapter(List<WeatherData> list) {
            this.list = list;
        }

        @Override
        public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            WeatherViewHolder holder = new WeatherViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(WeatherViewHolder holder, int position) {
            WeatherData weatherData = list.get(position);
            int imageId = getResources()
                    .getIdentifier("com.danielsolawa.locationapp:drawable/"
                                    + weatherData.getIcon()
                            , null, null);


            holder.icon.setImageResource(imageId);

            holder.tempTv.setText(String.format("%s \u00b0C",
                    weatherData.getTemp()));
            holder.dateTv.setText(DateUtils.getLocalizedDate(weatherData.getDate()));

            if(darkMode){
                holder.tempTv.setTextColor(Color.WHITE);
                holder.dateTv.setTextColor(Color.WHITE);
            }

        }



        private String convertSpeed(double windSpeed) {
            double convertedSpeed = (windSpeed * 3600) / 1000;
            DecimalFormat dc = new DecimalFormat("###.##");


            return dc.format(convertedSpeed);
        }

        @Override
        public int getItemCount() {
            return listData.size();
        }
    }

    public class WeatherViewHolder extends  RecyclerView.ViewHolder{
        public ImageView icon;
        public TextView dateTv;
        public TextView tempTv;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon_rv);
            dateTv = (TextView) itemView.findViewById(R.id.date_rv);
            tempTv = (TextView) itemView.findViewById(R.id.temp_rv);
        }
    }
}
