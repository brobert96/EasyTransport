package com.example.robert.easytransport.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by Robert on 11/6/2017.
 */

public class DirectionResult {

    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }


public class Route {
    @SerializedName("overview_polyline")
    public OverviewPolyline polyline;

    private List<Legs> legs;

    public OverviewPolyline getPolyline() {
        return polyline;
    }

    public List<Legs> getLegs() {
        return legs;
    }
}

public class Legs {



    @SerializedName("duration")
    private Duration duration;



    public Duration getDuration() {
        return duration;
    }
}


public class Duration{
    @SerializedName("value")
    private int value;

    public int getValue() {
        return value;
    }
}

    public class OverviewPolyline {
        @SerializedName("points")
        public String points;

        public String getPoints() {
            return points;
        }
    }
}

