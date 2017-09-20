package com.danielsolawa.locationapp.adapter;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.model.WeatherData;
import com.danielsolawa.locationapp.utils.Constants;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeverForgive on 2017-09-13.
 */

public class HorizontalListViewFragment extends Fragment {

    public static final String TAG = HorizontalListViewFragment.class.getSimpleName();
    private List<WeatherData> listData = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parcelable list = getArguments().getParcelable(Constants.WEATHER_DATA_LIST);
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
            holder.localityTv.setText(weatherData.getLocality().getName());
            holder.descTv.setText(weatherData.getDescription());
            holder.dateTv.setText(weatherData.getDate());
        }

        @Override
        public int getItemCount() {
            return listData.size();
        }
    }

    public class WeatherViewHolder extends  RecyclerView.ViewHolder{
        public ImageView icon;
        public TextView localityTv;
        public TextView descTv;
        public TextView dateTv;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon_rv);
            localityTv = (TextView) itemView.findViewById(R.id.locality_rv);
            descTv = (TextView) itemView.findViewById(R.id.desc_rv);
            dateTv = (TextView) itemView.findViewById(R.id.date_rv);
        }
    }
}
