package com.example.robert.easytransport.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.adapters.NewStationAdapter;
import com.example.robert.easytransport.models.BusStation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Robert on 11/16/2017.
 */

public class ListOfNewStations extends Fragment {

    @BindView(R.id.new_stations_list)
    RecyclerView recyclerView;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_of_new_stations_layout, container, false);
        ButterKnife.bind(this, v);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("newStations");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<BusStation> newStations = new ArrayList<BusStation>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    BusStation busStation = postSnapshot.getValue(BusStation.class);
                    busStation.setID(postSnapshot.getKey());
                    newStations.add(busStation);
                }

                LinearLayoutManager lm = new LinearLayoutManager(getActivity());
                NewStationAdapter adapter = new NewStationAdapter(newStations, new OnClickDeleteButtonCallback() {
                    @Override
                    public void onDelete(BusStation station) {
                        databaseReference = firebaseDatabase.getReference("busStations");
                        databaseReference.child(station.getID()).child("buses").setValue(station.getBuses());
                        databaseReference.child(station.getID()).child("latitude").setValue(station.getLatitude());
                        databaseReference.child(station.getID()).child("longitude").setValue(station.getLongitude());
                        databaseReference.child(station.getID()).child("name").setValue(station.getName());
                        databaseReference = firebaseDatabase.getReference("newStations");
                        databaseReference.child(station.getID()).removeValue();
                    }
                }, new OnClickAcceptButtonCallback() {
                    @Override
                    public void onAccept(BusStation station) {
                        databaseReference = firebaseDatabase.getReference("newStations");
                        databaseReference.child(station.getID()).removeValue();
                    }
                });
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(lm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    public interface OnClickDeleteButtonCallback{
        public void onDelete(BusStation station);
    }
    public interface OnClickAcceptButtonCallback{
        public void onAccept(BusStation station);
    }
}
