package com.example.robert.easytransport.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Robert on 10/18/2017.
 */

public class BusStationJsonModel {

    @SerializedName("name")
    private String name;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("buses")
    private String buses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBuses() {
        return buses;
    }

    public void setBuses(String buses) {
        this.buses = buses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("***** BusStation Details*****\n");
        sb.append("Name="+getName()+"\n");
        sb.append("Location="+getLongitude().toString()+"\n");
        sb.append("Location="+getLatitude().toString()+"\n");
        sb.append("Buses="+getBuses()+"\n");
        return sb.toString();
    }
}
