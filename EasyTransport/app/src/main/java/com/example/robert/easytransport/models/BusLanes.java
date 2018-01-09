package com.example.robert.easytransport.models;

import java.util.ArrayList;

/**
 * Created by Robert on 11/9/2017.
 */

public class BusLanes {

    private ArrayList<BusStation> laneStations;
    private String number;
    private String waypoints;
    private String goodWaypoints;
    private String middle = "";
    private ArrayList<BusStation> allStation;
    private static final String TAG = BusLanes.class.getSimpleName();


    public BusLanes() {
        laneStations = new ArrayList<>();
    }
    public BusLanes(BusLanes b) {
        allStation = new ArrayList<>();
        laneStations = new ArrayList<>();
        this.number = b.getNumber();
        this.waypoints = b.getWaypoints();
        this.middle = b.getMiddle();
        this.laneStations = b.getLaneStations();
    }

    public ArrayList<BusStation> getAllStation() {
        return allStation;
    }

    public void setAllStation(ArrayList<BusStation> allStation) {
        this.allStation = allStation;
        transformWaypointString();
    }

    public ArrayList<BusStation> getLaneStations() {
        return laneStations;
    }

    public String getNumber() {
        return number;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setLaneStations(ArrayList<BusStation> laneStations) {
        this.laneStations = laneStations;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public void transformWaypointString(){
        String[] temp = waypoints.split(",");
        for(String s:temp){
            for(BusStation station : allStation){
                if(s.equals(station.getID())){
                    laneStations.add(station);
                }
            }
        }
    }

    public String getGoodWaypoints() {
        return goodWaypoints;
    }

    public void setGoodWaypoints(final ArrayList<BusStation> goodWaypoint) {
        goodWaypoints = "";
        for(BusStation s : goodWaypoint){
            goodWaypoints += s.getLatitude()+","+s.getLongitude()+"|";
        }
        if(goodWaypoints.length()>0){
            goodWaypoints.substring(0, goodWaypoints.length()-1);
        }
    }
}