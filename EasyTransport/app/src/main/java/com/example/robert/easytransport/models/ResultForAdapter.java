package com.example.robert.easytransport.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Robert on 11/13/2017.
 */

public class ResultForAdapter {

    private BusStation station1;
    private BusStation station2;
    private BusLanes lane;
    private List<LatLng> polylineLatLng;
    private int duration;

    public ResultForAdapter() {
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<LatLng> getPolylineLatLng() {
        return polylineLatLng;
    }

    public void setPolylineLatLng(List<LatLng> polylineLatLng) {
        this.polylineLatLng = polylineLatLng;
    }

    public BusStation getStation1() {
        return station1;
    }

    public void setStation1(BusStation station1) {
        this.station1 = station1;
    }

    public BusStation getStation2() {
        return station2;
    }

    public void setStation2(BusStation station2) {
        this.station2 = station2;
    }

    public BusLanes getLane() {
        return lane;
    }

    public void setLane(BusLanes lane) {
        this.lane = lane;
    }
}
