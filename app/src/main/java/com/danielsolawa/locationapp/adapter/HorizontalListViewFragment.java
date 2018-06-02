package com.danielsolawa.locationapp.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    //views
    private LinearLayout iconLayout;
    private LinearLayout detailsLayout;
    private FrameLayout cardsLayout;
    private Button closeButton;
    private TextView desc;
    private TextView date;
    private TextView windSpeed;
    private TextView temp;
    private TextView pressure;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parcelable list = getArguments().getParcelable(Constants.WEATHER_DATA_LIST);
        darkMode = getArguments().getBoolean(Constants.DARK_MODE);
        listData = Parcels.unwrap(list);

        initViews();

    }

    private void initViews() {
        iconLayout = (LinearLayout) getActivity().findViewById(R.id.details_icon_layout);
        detailsLayout = (LinearLayout) getActivity().findViewById(R.id.details_layout);
        cardsLayout = (FrameLayout) getActivity().findViewById(R.id.fragmentContainer);
        closeButton = (Button) getActivity().findViewById(R.id.details_button);
        desc = (TextView) getActivity().findViewById(R.id.details_tv_desc);
        date = (TextView) getActivity().findViewById(R.id.details_tv_date);
        windSpeed = (TextView) getActivity().findViewById(R.id.details_tv_wind);
        temp = (TextView) getActivity().findViewById(R.id.details_tv_temp);
        pressure = (TextView) getActivity().findViewById(R.id.details_tv_pressure);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDetails(false);
            }
        });
    }

    private void toggleDetails(boolean show) {
        if(show){
            cardsLayout.setVisibility(View.GONE);
            detailsLayout.setVisibility(View.VISIBLE);
        }else{
            cardsLayout.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.GONE);
        }
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
        public void onBindViewHolder(WeatherViewHolder holder, final int position) {
            final WeatherData weatherData = list.get(position);
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
                temp.setTextColor(Color.WHITE);
                desc.setTextColor(Color.WHITE);
                pressure.setTextColor(Color.WHITE);
                date.setTextColor(Color.WHITE);
                windSpeed.setTextColor(Color.WHITE);
                closeButton.setTextColor(Color.WHITE);
            }

            holder.cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleDetails(true);
                    temp.setText(String.format("%s \u00b0C",
                            weatherData.getTemp()));
                    desc.setText(weatherData.getDescription());
                    pressure.setText(String.format("%s hPa",
                            weatherData.getPressure()));
                    windSpeed.setText(String.format("%s km/h",
                            weatherData.getWindSpeed()));
                    date.setText(DateUtils.getLocalizedDate(weatherData.getDate())
                            + "\n" + DateUtils.getDateOnly(weatherData.getDate()));


                    int imgId = getResources()
                            .getIdentifier("com.danielsolawa.locationapp:drawable/"
                                            + weatherData.getIcon()
                                    , null, null);
                    iconLayout.setBackground(getActivity().getDrawable(imgId));
                }
            });

        }




        @Override
        public int getItemCount() {
            return listData.size();
        }
    }

    public class WeatherViewHolder extends  RecyclerView.ViewHolder{
        public LinearLayout cardLayout;
        public ImageView icon;
        public TextView dateTv;
        public TextView tempTv;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.card_layout);
            icon = (ImageView) itemView.findViewById(R.id.icon_rv);
            dateTv = (TextView) itemView.findViewById(R.id.date_rv);
            tempTv = (TextView) itemView.findViewById(R.id.temp_rv);


        }
    }
}
