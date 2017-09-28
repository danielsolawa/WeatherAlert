package com.danielsolawa.locationapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.parceler.Parcel;



/**
 * Created by NeverForgive on 2017-09-01.
 */

@Table(name = "weather_data")
@Parcel(value = Parcel.Serialization.BEAN, analyze = WeatherData.class)
public class WeatherData extends Model{

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private String date;

    @Column(name = "icon")
    private String icon;

    @Column(name = "temp")
    private double temp;

    @Column(name = "pressure")
    private double pressure;

    @Column(name = "visibility")
    private double visibility;

    @Column(name = "wind_speed")
    private double windSpeed;


    @Column(name = "locality",
            onDelete = Column.ForeignKeyAction.CASCADE,
            onUpdate = Column.ForeignKeyAction.CASCADE)
    private Locality locality;



    public WeatherData() {
        super();
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }


    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", icon='" + icon + '\'' +
                ", temp=" + temp +
                ", pressure=" + pressure +
                ", visibility=" + visibility +
                ", windSpeed=" + windSpeed +
                ", locality=" + locality +
                '}';
    }
}
