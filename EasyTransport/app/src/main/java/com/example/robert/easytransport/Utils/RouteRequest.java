package com.example.robert.easytransport.Utils;

import com.example.robert.easytransport.models.DirectionResult;

import org.json.JSONException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Robert on 12/4/2017.
 */

public class RouteRequest {

    private static final String DISTANCE_REQUEST_API_KEY = "AIzaSyCO09fqZmYS8WLZVYVN5ez7wgQnjN7kziQ";

    private interface DirectionsApiService{
        @GET("/maps/api/directions/json")
        Call<DirectionResult> getJson(@Query("origin") String origin, @Query("destination") String destination, @Query("waypoints") String waypoints, @Query("mode") String mode, @Query("key") String key);
    }


    public static DirectionResult.Route getDistanceInfo(String latFrom, String lngFrom, String latTo, String lngTo, String waypoints, String mode) throws JSONException, IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionsApiService d = retrofit.create(DirectionsApiService.class);
        DirectionResult route = d.getJson(latFrom+","+lngFrom, latTo+","+lngTo, waypoints, mode, DISTANCE_REQUEST_API_KEY).execute().body();

        if(route.getRoutes().size()>0){
            return route.getRoutes().get(0);
        }
        return null;
    }
}
