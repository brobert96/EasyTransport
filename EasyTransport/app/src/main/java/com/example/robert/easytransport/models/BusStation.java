package com.example.robert.easytransport.models;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Robert on 10/3/2017.
 */



public class BusStation implements Parcelable{

    private String ID;
    private String name;
    private String longitude;
    private String latitude;
    private String buses;

    public BusStation(){

    }

    protected BusStation(Parcel in) {
        ID = in.readString();
        name = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        buses = in.readString();
    }

    public static final Creator<BusStation> CREATOR = new Creator<BusStation>() {
        @Override
        public BusStation createFromParcel(Parcel in) {
            return new BusStation(in);
        }

        @Override
        public BusStation[] newArray(int size) {
            return new BusStation[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBuses() {
        return buses;
    }

    public void setBuses(String buses) {
        this.buses = buses;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeString(name);
        parcel.writeString(longitude);
        parcel.writeString(latitude);
        parcel.writeString(buses);
    }
}
