package com.example.robert.easytransport.models;

import android.util.Log;
import java.util.ArrayList;


/**
 * Created by Robert on 11/9/2017.
 */

public class BusStationPair {

    private BusStation station1;
    private BusStation station2;
    private Boolean flag;
    private ArrayList<BusLanes> commonLanes;
    private ArrayList<BusLanes> goodLanes;
    private ArrayList<BusLanes> allLanes;
    private static final String TAG = BusStationPair.class.getSimpleName();

    public BusStationPair() {
    }

    public BusStationPair(BusStation station1, BusStation station2, ArrayList<BusLanes> allLanes) {
        if (allLanes.size()>0){
            this.allLanes = new ArrayList<>();
            for (BusLanes all : allLanes){
                BusLanes l = new BusLanes(all);
                Log.d(TAG, all.getNumber()+","+l.getNumber());
                this.allLanes.add(l);
            }
            Log.d(TAG, this.allLanes.size()+"");
        }else {
            this.allLanes = allLanes;
        }
        this.station1 = station1;
        this.station2 = station2;
        commonLanes = new ArrayList<>();
        goodLanes = new ArrayList<>();
        checkCommonLanes(station1.getBuses(), station2.getBuses());
        checkGoodPair(station1.getID(), station2.getID());
        if(goodLanes.size()>0){
            flag=true;
        }else {
            flag=false;
        }
    }

    private void checkCommonLanes(String first, String second){
        String[] firstLanes = first.split(",");
        String[] secondLanes = second.split(",");

        for (String s :firstLanes){
            for (String s1 : secondLanes){
                if (s.equals(s1)){
                    for(BusLanes bl : allLanes){
                        if(bl.getNumber().equals(s)){
                            commonLanes.add(bl);
                        }
                    }
                    Log.d(TAG, "commonLanes : " + s);
                }
            }
        }
    }


    public void checkGoodPair(String ID1, String ID2){
        boolean flag1 = false;

        ArrayList<BusStation> temp = new ArrayList<>();

        Log.d(TAG, "CHECKGOODPAIR : "+station1.getName()+","+station2.getName());


            for (BusLanes busLane : commonLanes){
                temp.clear();
                flag1=false;
                Log.d(TAG, "Next search!");
                    for (BusStation station : busLane.getLaneStations()){
                        if(station.getID().equals(busLane.getMiddle()) && flag1 == true){

                            if(!busLane.getMiddle().equals(ID2)){
                                break;
                            }
                        }
                        if(flag1 || station.getID().equals(ID1)){
                            temp.add(station);
                            Log.i(TAG, station.getName());
                            flag1=true;
                            if (station.getID().equals(ID2)){
                                Log.i(TAG, "Found route!");
                                temp.remove(0);
                                temp.remove(station);
                                busLane.setGoodWaypoints(temp);
                                goodLanes.add(busLane);
                                temp.clear();
                                break;
                            }
                        }
                    }
                if(goodLanes.isEmpty()){
                    Log.d(TAG, "GoodLanes is Empty!!!!!!!!!!!!!!!!!!!!!!!!");
                        temp.clear();
                        flag1 = false;
                        Log.d(TAG, "Next search!");
                        for (BusStation station : busLane.getLaneStations()){
                            if(station.getID().equals(ID1) || flag1){
                                temp.add(station);
                                flag1 = true;
                                Log.d(TAG, station.getName());
                                if (station.getID().equals(ID2)){
                                    Log.i(TAG, "Found route!");
                                    temp.remove(0);
                                    temp.remove(station);
                                    busLane.setGoodWaypoints(temp);
                                    goodLanes.add(busLane);
                                    temp.clear();
                                    break;
                                }
                            }
                        }
                    }
            }


        return;
    }

    public BusStation getStation1() {
        return station1;
    }

    public BusStation getStation2() {
        return station2;
    }

    public Boolean getFlag() {
        return flag;
    }

    public ArrayList<BusLanes> getGoodLanes() {
        return goodLanes;
    }


}
