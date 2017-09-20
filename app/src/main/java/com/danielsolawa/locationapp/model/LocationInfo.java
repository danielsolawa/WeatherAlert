package com.danielsolawa.locationapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NeverForgive on 2017-08-24.
 */

public class LocationInfo implements Parcelable{

    private String locality;
    private double latitude;
    private double longitude;
    private String adminArea;
    private String subAdminArea;
    private String postalCode;
    private String countryName;

    public LocationInfo() {
    }

    public LocationInfo(String locality, double latitude, double longitude, String adminArea,
                        String subAdminArea, String postalCode, String countryName) {
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adminArea = adminArea;
        this.subAdminArea = subAdminArea;
        this.postalCode = postalCode;
        this.countryName = countryName;
    }

    private LocationInfo(Parcel in) {
        locality = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        adminArea = in.readString();
        subAdminArea = in.readString();
        postalCode = in.readString();
        countryName = in.readString();
    }

    public static final Creator<LocationInfo> CREATOR = new Creator<LocationInfo>() {
        @Override
        public LocationInfo createFromParcel(Parcel in) {
            return new LocationInfo(in);
        }

        @Override
        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locality);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(adminArea);
        dest.writeString(subAdminArea);
        dest.writeString(postalCode);
        dest.writeString(countryName);
    }


    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
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

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public void setSubAdminArea(String subAdminArea) {
        this.subAdminArea = subAdminArea;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "locality='" + locality + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", adminArea='" + adminArea + '\'' +
                ", subAdminArea='" + subAdminArea + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", countryName='" + countryName + '\'' +
                '}';
    }
}
