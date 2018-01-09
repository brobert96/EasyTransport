package com.example.robert.easytransport.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.Utils.CheckBestRouteTask;
import com.example.robert.easytransport.Utils.RouteCalculationMain;
import com.example.robert.easytransport.adapters.BusStationResultAdapter;
import com.example.robert.easytransport.data.SharedPrefs;
import com.example.robert.easytransport.models.BusLanes;
import com.example.robert.easytransport.models.BusStation;
import com.example.robert.easytransport.models.BusStationPair;
import com.example.robert.easytransport.models.DirectionResult;
import com.example.robert.easytransport.models.ResultForAdapter;
import com.example.robert.easytransport.receivers.ArrivedReceiver;
import com.example.robert.easytransport.services.GPSTracker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.robert.easytransport.Utils.RouteRequest.getDistanceInfo;


/**
 * Created by Robert on 9/29/2017.
 */

public class MainFragment extends Fragment implements OnMapReadyCallback {

    private static final int zoom = 13;
    private static final int animate_time = 1000;
    private static final String ME = "me";
    private static final String OBJECTIVE = "objective";
    private static final String TAG = MainFragment.class.getSimpleName();




    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.cardText)
    TextView cardTextView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.station_result_list)
    RecyclerView stationRecyclerView;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slideingPanel;
    @BindView(R.id.swipe_text)
    TextView swipeText;
    @BindView(R.id.navigate)
    Button navigateButton;

    private ArrivedReceiver myReceiver;
    private Unbinder unbinder;
    private GoogleMap map;
    private Marker placeMarker;
    private Marker busMarker1;
    private Marker busMarker2;
    private Location myLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private DatabaseReference dtbRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    private GeoDataClient client;
    private Place tempPlace;
    private ArrayList<BusStation> stationsNearMe;
    private ArrayList<BusStationPair> pairs;
    private ArrayList<ResultForAdapter> result;
    private Polyline line;
    private ArrayList<BusStation> allStations;
    DatabaseReference ref;
    private CheckBestRouteTask task;
    RouteCalculationMain routeCalculationMain;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.homemapfragment_layout, container, false);

        unbinder = ButterKnife.bind(this, v);

        client = Places.getGeoDataClient(getActivity(), null);

        result = new ArrayList<ResultForAdapter>();
        stationsNearMe = new ArrayList<BusStation>();
        pairs = new ArrayList<>();
        allStations = SharedPrefs.getsInstance(getActivity()).getStations();

        myReceiver = new ArrivedReceiver();
        slideingPanel.setTouchEnabled(false);


        Log.d(TAG, allStations.size() + "");

        firebaseDatabase = FirebaseDatabase.getInstance();
        dtbRef = firebaseDatabase.getReference("busStations");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        placeAutocompleteFragment = (PlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.searchv);

        PlaceSelectionListener placeSelectionListener = new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                navigateButton.setVisibility(View.GONE);
                slideingPanel.setTouchEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                tempPlace = place;
                routeCalculationMain.removeMarkers();
                routeCalculationMain.putAddressMarker(place, placeMarker);
                int distance;
                String startAt;
                String endAt;
                Location location1 = new Location("");
                location1.setLatitude((place.getLatLng()).latitude);
                location1.setLongitude((place.getLatLng()).longitude);
                distance = RouteCalculationMain.distanceCalculator(location1);
                startAt = (distance - 600) + "";
                endAt = (distance + 600) + "";
                retrieveDataFromDatabase(startAt, endAt, OBJECTIVE);
                auth = FirebaseAuth.getInstance();
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            ArrayList<String> needToBeDeleted = new ArrayList<String>();
                            String alreadySearched = null;
                            boolean flag = false;
                            int sum = 0;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                sum++;
                                if (Long.parseLong(postSnapshot.getKey()) < System.currentTimeMillis()) {
                                    needToBeDeleted.add(postSnapshot.getKey());
                                }
                                if (postSnapshot.getValue().toString().equals(place.getId())) {
                                    alreadySearched = postSnapshot.getKey();
                                    flag = true;
                                }
                            }
                            ref.child(auth.getCurrentUser().getUid()).removeEventListener(this);
                            DatabaseReference ref1 = firebaseDatabase.getReference("userQueries").child(auth.getCurrentUser().getUid());
                            if (flag) {
                                ref1.child(alreadySearched).removeValue();
                            }
                            Comparator comparator = new Comparator<String>() {
                                @Override
                                public int compare(String s, String t1) {
                                    return s.compareTo(t1);
                                }
                            };
                            Collections.sort(needToBeDeleted, comparator);
                            if (sum > 5 && !needToBeDeleted.get(0).equals(alreadySearched)) {
                                ref1.child(needToBeDeleted.get(0)).removeValue();
                            }
                            Log.d(TAG, "If not null");
                            ref1.child(System.currentTimeMillis() + "").setValue(place.getId());
                        } else {
                            Log.d(TAG, "If null");
                            ref.child(auth.getCurrentUser().getUid()).removeEventListener(this);
                            ref.child(auth.getCurrentUser().getUid()).child(System.currentTimeMillis() + "").setValue(place.getId());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                if (auth.getCurrentUser() != null) {
                    ref = firebaseDatabase.getReference("userQueries");
                    ref.child(auth.getCurrentUser().getUid()).addValueEventListener(valueEventListener);
                }
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
                cardTextView.setText(getString(R.string.no_search_result));
            }
        };
        placeAutocompleteFragment.setOnPlaceSelectedListener(placeSelectionListener);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        MapsInitializer.initialize(getActivity());


        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        routeCalculationMain = new RouteCalculationMain(map);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        final Bundle bundle = this.getArguments();
        OnSuccessListener onSuccessListener = new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13f), 1000, null);
                    int distance = RouteCalculationMain.distanceCalculator(myLocation);
                    String startAt = (distance-600)+"";
                    String endAt = (distance+600)+"";
                    retrieveDataFromDatabase(startAt,endAt, ME);
                    if(bundle != null){
                        if(bundle.getString("placeID") != null){
                            progressBar.setVisibility(View.VISIBLE);
                            String placeId = bundle.getString("placeID");
                            client.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                    Place p = task.getResult().get(0);
                                    tempPlace = p;
                                    Location l = new Location("");
                                    l.setLatitude(p.getLatLng().latitude);
                                    l.setLongitude(p.getLatLng().longitude);
                                    int distance = RouteCalculationMain.distanceCalculator(l);
                                    retrieveDataFromDatabase((distance-600)+"", (distance+600)+"", OBJECTIVE);
                                    routeCalculationMain.putAddressMarker(p, placeMarker);
                                }
                            });
                        }
                        if(bundle.getString("busStop") != null){
                            for(BusStation bs : allStations){
                                if(bs.getID().equals(bundle.getString("busStop"))){
                                    LatLng latLng = new LatLng(Double.parseDouble(bs.getLatitude()),Double.parseDouble(bs.getLongitude()));
                                    routeCalculationMain.setBusMarkers(busMarker1,latLng, "Buslanes: "+bs.getBuses());
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f), 1000, null);
                                }
                            }
                        }
                        bundle.clear();
                    }
                }


            }
        };
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), onSuccessListener);

        placeMarker = map.addMarker(new MarkerOptions().position(new LatLng(-84.651365, 58.359361)));
        busMarker1 = map.addMarker(new MarkerOptions().position(new LatLng(-84.651365, 58.359361)));
        busMarker2 = map.addMarker(new MarkerOptions().position(new LatLng(-84.651365, 58.359361)));
        line = map.addPolyline(new PolylineOptions().add(new LatLng(-84.651365, 58.359361)));

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(task != null){
            task.cancel(true);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void searchForBuses(final ArrayList<ResultForAdapter> result) {
        OnFindBusesCallback onFindBusesCallback = new OnFindBusesCallback() {
            @Override
            public void putMarkers() {

                Collections.sort(result, new Comparator<ResultForAdapter>() {
                    @Override
                    public int compare(ResultForAdapter t1, ResultForAdapter t2) {
                        if(t1.getDuration()<t2.getDuration()){return -1;}
                        if(t1.getDuration()>t2.getDuration()){return 1;}
                        return 0;
                    }
                });

                for(ResultForAdapter res : result){
                    Log.d(TAG, res.getStation1().getName()+", "+res.getStation2().getName()+", "+res.getLane().getNumber());
                }

                BusStationResultAdapter adapter;
                LinearLayoutManager lm = new LinearLayoutManager(getActivity());
                adapter = new BusStationResultAdapter(result, new OnClickBuses() {
                    @Override
                    public void setMarkers(int position) {
                        setBusMarkers(position);
                    }
                });
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(stationRecyclerView.getContext(), lm.getOrientation());
                dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.decoration_stroke));
                stationRecyclerView.addItemDecoration(dividerItemDecoration);
                stationRecyclerView.setAdapter(adapter);
                stationRecyclerView.setLayoutManager(lm);
                slideingPanel.setTouchEnabled(true);
                slideingPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {

                    }

                    @Override
                    public void onPanelCollapsed(View panel) {

                    }

                    @Override
                    public void onPanelExpanded(View panel) {

                    }

                    @Override
                    public void onPanelAnchored(View panel) {

                    }

                    @Override
                    public void onPanelHidden(View panel) {

                    }
                });

                progressBar.setVisibility(View.GONE);
                swipeText.setVisibility(View.VISIBLE);
            }
        };
        if(task != null){
            if(task.getStatus()== AsyncTask.Status.RUNNING){
                task.cancel(true);
            }
        }
        task = new CheckBestRouteTask(myLocation, tempPlace, result, onFindBusesCallback, this);
        task.execute();
        }

        public void setBusMarkers(final int position){
            slideingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            swipeText.setVisibility(View.GONE);
            navigateButton.setVisibility(View.VISIBLE);
            navigateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri myNavigationUri = Uri.parse("http://maps.google.com/maps?daddr="+result.get(position).getStation1().getLatitude()+","+result.get(position).getStation1().getLongitude()+"&mode=w");
                    Intent intent = new Intent(Intent.ACTION_VIEW, myNavigationUri);
                    startActivity(intent);
                    Intent intent1 = new Intent(getActivity(), GPSTracker.class);
                    intent1.putExtra("Latitude", result.get(position).getStation2().getLatitude()+"");
                    intent1.putExtra("Longitude", result.get(position).getStation2().getLongitude()+"");
                    myReceiver.setLatitude(tempPlace.getLatLng().latitude);
                    myReceiver.setLongitude(tempPlace.getLatLng().longitude);
                    getActivity().startService(intent1);
                    getActivity().registerReceiver(myReceiver, new IntentFilter("com.example.robert.easytransport.services.GPSTracker"));
                    navigateButton.setVisibility(View.GONE);
                }
            });


            PolylineOptions polylineOptions = new PolylineOptions();
            List<LatLng> polypoints = result.get(position).getPolylineLatLng();
            polylineOptions.add(polypoints.get(0));
            polylineOptions.width(10);
            polypoints.remove(0);
            for(LatLng ll : polypoints){
                if(ll != null){
                    polylineOptions.add(ll);
                }
            }
            routeCalculationMain.setPolyline(line, polylineOptions);
        }


        private void retrieveDataFromDatabase(final String startAt, String endAt, final String forWho){
            dtbRef = firebaseDatabase.getReference("busStations");
            if(forWho.equals(ME)) {
                dtbRef.orderByKey().startAt(startAt).endAt(endAt).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            BusStation station = postSnapshot.getValue(BusStation.class);
                            station.setID(postSnapshot.getKey());
                            stationsNearMe.add(station);
                        }
                        dtbRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }else if(forWho.equals(OBJECTIVE)){
                dtbRef.orderByKey().startAt(startAt).endAt(endAt).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<BusStation> stationsNearObjective = new ArrayList<BusStation>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            BusStation station = postSnapshot.getValue(BusStation.class);
                            station.setID(postSnapshot.getKey());
                            stationsNearObjective.add(station);
                        }
                        checkCloseStations(stationsNearObjective, stationsNearMe);
                        dtbRef.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        }

        private void checkCloseStations(ArrayList<BusStation> ob, ArrayList<BusStation> me){
            ArrayList<BusStation> finalMe = new ArrayList<>();
            ArrayList<BusStation> finalOb = new ArrayList<>();
            for(BusStation b1: me){
                Location l1 = new Location("");
                l1.setLatitude(Double.parseDouble(b1.getLatitude()));
                l1.setLongitude(Double.parseDouble(b1.getLongitude()));
                if(l1.distanceTo(myLocation)<500){
                    finalMe.add(b1);
                }
            }
            Location lob = new Location("");
            lob.setLatitude(tempPlace.getLatLng().latitude);
            lob.setLongitude(tempPlace.getLatLng().longitude);
            for(BusStation b2: ob){
                Location l1 = new Location("");
                l1.setLatitude(Double.parseDouble(b2.getLatitude()));
                l1.setLongitude(Double.parseDouble(b2.getLongitude()));
                if(l1.distanceTo(lob)<500){
                    finalOb.add(b2);
                }
            }
            checkBestStations(finalMe, finalOb);
        }

        private void checkBestStations(final ArrayList<BusStation> stationNearMe, final ArrayList<BusStation> stationNearOb){

            pairs.clear();
            result.clear();
            if(stationRecyclerView.getAdapter() != null){
                stationRecyclerView.getAdapter().notifyDataSetChanged();
            }
            swipeText.setVisibility(View.GONE);

            dtbRef = firebaseDatabase.getReference("busLanes");
            dtbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean closeFlag=false;
                    ArrayList<BusLanes> allLanes = new ArrayList<BusLanes>();
                    allLanes.clear();
                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        BusLanes lane = postSnapshot.getValue(BusLanes.class);
                        lane.setNumber(postSnapshot.getKey());
                        lane.setAllStation(allStations);
                        allLanes.add(lane);
                    }


                    ArrayList<BusStationPair> allPair = new ArrayList<>();
                    allPair.clear();
                    Location lo = new Location("");
                    lo.setLatitude(tempPlace.getLatLng().latitude);
                    lo.setLongitude(tempPlace.getLatLng().longitude);
                    if(myLocation.distanceTo(lo)>700){
                        for (BusStation b1: stationNearMe){
                            for (BusStation b2: stationNearOb){
                                if(!b1.getID().equals(b2.getID())){
                                    BusStationPair pair = new BusStationPair(b1, b2, allLanes);
                                    allPair.add(pair);
                                }
                            }
                        }
                    }else{
                        closeFlag = true;
                    }

                    ArrayList<BusStationPair> tempStationPairs = new ArrayList<BusStationPair>();
                    tempStationPairs.clear();
                    for (BusStationPair b2: allPair){
                        if(b2.getFlag()){
                            tempStationPairs.add(b2);
                        }
                    }


                    Collections.sort(tempStationPairs, new Comparator<BusStationPair>() {
                        @Override
                        public int compare(BusStationPair t1, BusStationPair t2) {
                            Location location1 = new Location("");
                            Location location2 = new Location("");
                            location1.setLatitude(Double.parseDouble(t1.getStation1().getLatitude()));
                            location1.setLongitude(Double.parseDouble(t1.getStation1().getLongitude()));
                            location2.setLatitude(Double.parseDouble(t2.getStation1().getLatitude()));
                            location2.setLongitude(Double.parseDouble(t2.getStation1().getLongitude()));
                            if(myLocation.distanceTo(location1)<myLocation.distanceTo(location2)){return -1;}
                            if(myLocation.distanceTo(location1)>myLocation.distanceTo(location2)){return 1;}
                            return 0;
                        }
                    });

                    pairs = tempStationPairs;
                    ArrayList<ResultForAdapter> tempResult = new ArrayList<>();

                    if(pairs.size()>0){

                        for (int i=0;i<pairs.size();i++){
                            for (BusLanes b : pairs.get(i).getGoodLanes()){
                                ResultForAdapter r = new ResultForAdapter();
                                r.setLane(b);
                                r.setStation1(pairs.get(i).getStation1());
                                r.setStation2(pairs.get(i).getStation2());
                                tempResult.add(r);
                            }
                        }


                        Location resLoc1 = new Location("");
                        Location resLoc2 = new Location("");
                        Location res2Loc1 = new Location("");
                        Location res2Loc2 = new Location("");
                        Location tempLoc = new Location("");
                        tempLoc.setLatitude(tempPlace.getLatLng().latitude);
                        tempLoc.setLongitude(tempPlace.getLatLng().longitude);
                        for(ResultForAdapter res : tempResult){
                            ResultForAdapter tempRes;
                            tempRes = res;
                            resLoc1.setLatitude(Double.parseDouble(res.getStation1().getLatitude()));
                            resLoc1.setLongitude(Double.parseDouble(res.getStation1().getLongitude()));
                            resLoc2.setLatitude(Double.parseDouble(res.getStation2().getLatitude()));
                            resLoc2.setLongitude(Double.parseDouble(res.getStation2().getLongitude()));
                            for(ResultForAdapter res2 : tempResult){
                                res2Loc1.setLatitude(Double.parseDouble(res2.getStation1().getLatitude()));
                                res2Loc1.setLongitude(Double.parseDouble(res2.getStation1().getLongitude()));
                                res2Loc2.setLatitude(Double.parseDouble(res2.getStation2().getLatitude()));
                                res2Loc2.setLongitude(Double.parseDouble(res2.getStation2().getLongitude()));
                                if(res.getLane().getNumber().equals(res2.getLane().getNumber())){
                                    int s1 = res.getLane().getGoodWaypoints().length();
                                    int s2 = res2.getLane().getGoodWaypoints().length();
                                    float distance1 = resLoc1.distanceTo(myLocation)+resLoc2.distanceTo(tempLoc);
                                    float distance2 = res2Loc1.distanceTo(myLocation)+res2Loc2.distanceTo(tempLoc);
                                    if(Math.abs(s1-s2)<3){
                                        if(distance1>distance2){
                                            tempRes = res2;
                                        }
                                    }
                                }
                            }
                            boolean contains = true;
                            for (ResultForAdapter r : result){
                                if(r.getLane().getNumber().equals(tempRes.getLane().getNumber())){
                                    contains = false;
                                    break;
                                }
                            }
                            if(contains){
                                result.add(tempRes);
                            }
                        }


                        if (result.size()<2){
                            cardTextView.setText("You can take "+result.get(0).getLane().getNumber()+" bus(es) from the "+result.get(0).getStation1().getName() + ". Wait for the time calculation!");
                        }else{
                            cardTextView.setText(getString(R.string.multiple_results));
                        }

                        searchForBuses(result);

                    }else {
                        if(closeFlag){
                            cardTextView.setText(getString(R.string.close_to_destination));
                        }else {
                            cardTextView.setText(getString(R.string.no_routs_found));
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    dtbRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    public interface OnFindBusesCallback{
        public void putMarkers();
    }

    public interface OnClickBuses{
        public void setMarkers(int position);
    }

    public int checkDuration(BusStation s1, BusStation s2, BusLanes lane, Location myLocation, Place tempPlace){

        int fromMe=0;
        int betweenStations=0;
        int toDestionation=0;

        DirectionResult.Route leg1;
        DirectionResult.Route leg2;
        DirectionResult.Route leg3;


        String waypoints = lane.getGoodWaypoints();

        try {
            leg1 = getDistanceInfo(myLocation.getLatitude()+"", myLocation.getLongitude()+"", s1.getLatitude(), s1.getLongitude(), "", "walking");
            leg2 = getDistanceInfo(s1.getLatitude(), s1.getLongitude(), s2.getLatitude(), s2.getLongitude(), waypoints, "driving");
            leg3 = getDistanceInfo(s2.getLatitude(), s2.getLongitude(), tempPlace.getLatLng().latitude+"", tempPlace.getLatLng().longitude+"", "", "walking");
            if(leg1==null || leg2==null || leg3==null){
                return 1000000;
            }
            fromMe = leg1.getLegs().get(0).getDuration().getValue();
            for(DirectionResult.Legs leg : leg2.getLegs()){
                betweenStations += leg.getDuration().getValue();
            }
            toDestionation = leg3.getLegs().get(0).getDuration().getValue();

            double d = betweenStations*0.8;
            betweenStations = ((int) d);

            List<LatLng> polylines = PolyUtil.decode(leg1.getPolyline().getPoints());
            polylines.addAll(PolyUtil.decode(leg2.getPolyline().getPoints()));
            polylines.addAll(PolyUtil.decode(leg3.getPolyline().getPoints()));


            for (int i=0;i<result.size();i++){
                if(result.get(i).getLane().equals(lane) && result.get(i).getStation1().equals(s1) && result.get(i).getStation2().equals(s2)){
                    result.get(i).setPolylineLatLng(polylines);
                    result.get(i).setDuration(fromMe+betweenStations+toDestionation);
                    break;
                }
            }

            return fromMe+betweenStations+toDestionation;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}

