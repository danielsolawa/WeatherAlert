package com.danielsolawa.locationapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeverForgive on 2017-08-28.
 */

@Table(name = "locality")
@Parcel(value = Parcel.Serialization.BEAN, analyze = {Locality.class})
public class Locality extends Model {

    @Column(name = "name", unique = true, onUniqueConflict = Column.ConflictAction.FAIL)
    private String name;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;



    public Locality() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public List<Alert> getAlerts(){
        if(getId() > 0){
            return getMany(Alert.class, "locality");
        }

        return new ArrayList<Alert>();
    }

    @Override
    public String toString() {
        return "Locality{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
