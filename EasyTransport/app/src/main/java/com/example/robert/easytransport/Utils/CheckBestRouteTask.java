package com.example.robert.easytransport.Utils;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.example.robert.easytransport.fragments.MainFragment;
import com.example.robert.easytransport.models.BusLanes;
import com.example.robert.easytransport.models.BusStation;
import com.example.robert.easytransport.models.ResultForAdapter;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by Robert on 12/4/2017.
 */

public class CheckBestRouteTask extends AsyncTask<Void, Void, Pair<BusStation, BusStation>>{


    private static final String TAG = CheckBestRouteTask.class.getSimpleName();
    MainFragment.OnFindBusesCallback callback;
    MainFragment mf;
    Location myLocation;
    Place tempPlace;
    ArrayList<ResultForAdapter> result;

    public CheckBestRouteTask(Location myLocation, Place tempPlace, ArrayList<ResultForAdapter> result, MainFragment.OnFindBusesCallback callback, MainFragment context){
        this.myLocation = myLocation;
        this.result = result;
        this.mf = context;
        this.tempPlace = tempPlace;
        this.callback = callback;
    }

    BusLanes busLane;

    @Override
    protected Pair<BusStation, BusStation> doInBackground(Void... voids) {
        Pair<BusStation, BusStation> tempPair = null;
        Log.d(TAG, "Google request started!");
        int tempDuration = 100000;
        for (ResultForAdapter pair : result){
                int s = mf.checkDuration(pair.getStation1(), pair.getStation2(), pair.getLane(), myLocation, tempPlace);
                Log.d(TAG, pair.getLane().getGoodWaypoints());
                Log.d(TAG, pair.getStation1().getName()+", "+pair.getStation2().getName()+", "+pair.getLane().getNumber()+", "+s);
                if(s<tempDuration){
                    tempPair = new Pair<BusStation, BusStation>(pair.getStation1(), pair.getStation2());
                    busLane = pair.getLane();
                    tempDuration=s;
                }
        }

        return tempPair;
    }

    @Override
    protected void onPostExecute(Pair<BusStation, BusStation> busStationPair) {
        super.onPostExecute(busStationPair);
        if(callback != null){
            callback.putMarkers();
        }
    }

}
