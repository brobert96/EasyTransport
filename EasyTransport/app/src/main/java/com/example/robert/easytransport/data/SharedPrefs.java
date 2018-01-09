package com.example.robert.easytransport.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.robert.easytransport.models.BusStation;

import java.util.ArrayList;

/**
 * Created by Robert on 10/30/2017.
 */

public class SharedPrefs {

    Context context;
    SharedPreferences sp;
    private static SharedPrefs sInstance = null;
    private ArrayList<BusStation> stations;
    private Boolean isAdmin = false;
    private Boolean isServiceRuning;
    private Boolean calledFromAddBusStationActivity = false;

    private static final String firstRun = "firstrun";


    private SharedPrefs(Context context){
        this.context = context;
        sp = context.getSharedPreferences("Startup", Context.MODE_PRIVATE);
        stations = new ArrayList<>();
    }

    public Boolean getCalledFromAddBusStationActivity() {
        return calledFromAddBusStationActivity;
    }

    public void setCalledFromAddBusStationActivity(Boolean calledFromAddBusStationActivity) {
        this.calledFromAddBusStationActivity = calledFromAddBusStationActivity;
    }

    public Boolean getServiceRuning() {
        return isServiceRuning;
    }

    public void setServiceRuning(Boolean serviceRuning) {
        isServiceRuning = serviceRuning;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public static SharedPrefs getsInstance(Context context){
        if (sInstance == null){
            sInstance = new SharedPrefs(context);
        }
        return sInstance;
    }

    public Boolean getFirstRun() {
        return sp.getBoolean(firstRun, true);
    }
    public void setFirstRun(boolean bool){
        sp.edit().putBoolean(firstRun, bool).commit();
    }

    public ArrayList<BusStation> getStations() {
        return stations;
    }

    public void setStations(ArrayList<BusStation> stations) {
        this.stations = stations;
    }
}
