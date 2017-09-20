package com.danielsolawa.locationapp.bundle;

import android.os.Parcel;
import android.os.Parcelable;

import com.danielsolawa.locationapp.model.LocationInfo;

/**
 * Created by NeverForgive on 2017-08-25.
 */

public class WizardBundle implements Parcelable {

    private LocationInfo locationInfo;
    private String temperature;
    private String content;
    private int addLayoutVisibility;
    private int confirmLayoutVisibility;


    public WizardBundle() {
    }


    private WizardBundle(Parcel in) {
        locationInfo = in.readParcelable(LocationInfo.class.getClassLoader());
        temperature = in.readString();
        content = in.readString();
        addLayoutVisibility = in.readInt();
        confirmLayoutVisibility = in.readInt();
    }

    public static final Creator<WizardBundle> CREATOR = new Creator<WizardBundle>() {
        @Override
        public WizardBundle createFromParcel(Parcel in) {
            return new WizardBundle(in);
        }

        @Override
        public WizardBundle[] newArray(int size) {
            return new WizardBundle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(locationInfo, flags);
        dest.writeString(temperature);
        dest.writeString(content);
        dest.writeInt(addLayoutVisibility);
        dest.writeInt(confirmLayoutVisibility);
    }




    /*
    getters and setters
    */

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public String getTemperature() {
        return temperature == null ? "" : temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getAddLayoutVisibility() {
        return addLayoutVisibility;
    }

    public void setAddLayoutVisibility(int addLayoutVisibility) {
        this.addLayoutVisibility = addLayoutVisibility;
    }

    public int getConfirmLayoutVisibility() {
        return confirmLayoutVisibility;
    }

    public void setConfirmLayoutVisibility(int confirmLayoutVisibility) {
        this.confirmLayoutVisibility = confirmLayoutVisibility;
    }


    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
