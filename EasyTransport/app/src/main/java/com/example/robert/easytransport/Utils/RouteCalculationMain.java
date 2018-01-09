package com.example.robert.easytransport.Utils;

import android.location.Location;
import android.util.Log;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.models.BusLanes;
import com.example.robert.easytransport.models.BusStation;
import com.example.robert.easytransport.models.DirectionResult;
import com.example.robert.easytransport.models.ResultForAdapter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.robert.easytransport.Utils.RouteRequest.getDistanceInfo;

/**
 * Created by Robert on 12/4/2017.
 */

public class RouteCalculationMain {

    Marker busMarker1 = null;
    Marker busMarker2 = null;
    Marker placeMarker = null;
    Polyline line = null;
    GoogleMap map;

    private static final String TAG = RouteCalculationMain.class.getSimpleName();
    private static final int zoom = 13;
    private static final int animate_time = 1000;

    public RouteCalculationMain(GoogleMap map) {
        this.map = map;
    }

    public void putAddressMarker(Place place, Marker placeMarker) {
        this.placeMarker = placeMarker;
        LatLng latLng = place.getLatLng();
        this.placeMarker = map.addMarker(new MarkerOptions().position(latLng).title(place.getName().toString()));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), animate_time, null);
    }

    public void setBusMarkers(Marker busMarker1, LatLng latLng, String title){
        this.busMarker1 = busMarker1;
        BitmapDescriptor b = BitmapDescriptorFactory.fromResource(R.drawable.rsz_2bus_stop);
        this.busMarker1 = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(b));
    }

    public void setPolyline(Polyline line, PolylineOptions polylineOptions){
        this.line = line;
        this.line = map.addPolyline(polylineOptions);
    }

    public void removeMarkers(){
        if(this.placeMarker != null){
            this.placeMarker.remove();
        }
        if(this.busMarker2 != null){
            this.busMarker2.remove();
        }
        if(this.busMarker1 != null){
            this.busMarker1.remove();
        }
        if(this.line != null){
            this.line.remove();
        }
    }

    public static int distanceCalculator(Location location){
        Location origo = new Location("");
        origo.setLatitude(0.0);
        origo.setLongitude(0.0);
        return (int)location.distanceTo(origo);
    }


}
