package com.danielsolawa.locationapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.parceler.Parcel;

/**
 * Created by NeverForgive on 2017-08-28.
 */

@Table(name = "alert")
@Parcel(value = Parcel.Serialization.BEAN, analyze = {Alert.class})
public class Alert extends Model{

    @Column(name ="weather_condition")
    private String weatherCondition;

    @Column(name = "temperature_condition")
    private double temperatureCondition;

    @Column(name = "locality",
            onUpdate = Column.ForeignKeyAction.CASCADE,
            onDelete = Column.ForeignKeyAction.CASCADE)
    private Locality locality;

    public Alert() {
        super();
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public double getTemperatureCondition() {
        return temperatureCondition;
    }

    public void setTemperatureCondition(double temperatureCondition) {
        this.temperatureCondition = temperatureCondition;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    @Override
    public String
    toString() {
        return "Alert{" +
                "weatherCondition='" + weatherCondition + '\'' +
                ", temperatureCondition=" + temperatureCondition +
                ", locality=" + locality +
                '}';
    }
}
