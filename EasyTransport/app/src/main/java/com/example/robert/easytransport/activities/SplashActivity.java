package com.example.robert.easytransport.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.example.robert.easytransport.MainActivity;
import com.example.robert.easytransport.R;
import com.example.robert.easytransport.data.SharedPrefs;
import com.example.robert.easytransport.fragments.MainFragment;
import com.example.robert.easytransport.models.BusStation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity {

    private static int ALL_REQUESTED_PERMISSIONS;
    FirebaseAuth auth;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int WIFI_REQUEST_CODE = 1;
    Thread thread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        if (!isDataEnabled()){
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SplashActivity.this, R.style.AppTheme_NoActionBar));
            builder.setMessage(getString(R.string.turn_data));
            builder.setNeutralButton(getString(R.string.alert_button_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), WIFI_REQUEST_CODE);
                }
            });
            builder.show();
        }


        if(auth.getCurrentUser() != null){
            FirebaseDatabase fireData = FirebaseDatabase.getInstance();
            final DatabaseReference dataRef = fireData.getReference("admins");
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        if(auth.getCurrentUser().getUid().equals(postSnapshot.getValue().toString())){
                            SharedPrefs.getsInstance(getApplicationContext()).setAdmin(true);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("busStations");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<BusStation> allStations = new ArrayList<BusStation>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                BusStation station = postSnapshot.getValue(BusStation.class);
                                station.setID(postSnapshot.getKey());
                                allStations.add(station);
                            }
                            SharedPrefs.getsInstance(getApplicationContext()).setStations(allStations);
                            Log.d(TAG, allStations.size()+"");
                            if(auth.getCurrentUser() != null){
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, ALL_REQUESTED_PERMISSIONS);
        }
    }


    public boolean isDataEnabled(){
        ConnectivityManager cm = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WIFI_REQUEST_CODE) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!isDataEnabled()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SplashActivity.this, R.style.AppTheme_NoActionBar));
                        builder.setTitle(getString(R.string.connection_problem));
                        builder.setMessage(getString(R.string.turn_wifi_ormobile));
                        builder.setNeutralButton(getString(R.string.alert_button_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), WIFI_REQUEST_CODE);
                            }
                        });
                        builder.show();
                    }
                }
            });
            thread.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[2] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle(getString(R.string.permission_denied))
                    .setMessage(getString(R.string.need_permission))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(thread != null){
            thread.interrupt();
        }
    }
}

