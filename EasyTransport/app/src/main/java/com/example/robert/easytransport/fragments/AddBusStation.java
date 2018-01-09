package com.example.robert.easytransport.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.Utils.DialogBox;
import com.example.robert.easytransport.Utils.Utils;
import com.example.robert.easytransport.activities.LoginActivity;
import com.example.robert.easytransport.data.SharedPrefs;
import com.example.robert.easytransport.models.BusStation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Robert on 9/29/2017.
 */

public class AddBusStation extends Fragment {

    private static final String administratorEmail = "viberivals@gmail.com";

    @BindView(R.id.newStationText)
    TextView textView;
    @BindView(R.id.latitude)
    EditText inputLatitude;
    @BindView(R.id.longitude)
    EditText inputLongitde;
    @BindView(R.id.station_name)
    EditText inputStationName;
    @BindView(R.id.buses)
    EditText inputBuses;
    @BindView(R.id.new_stations_button)
    Button newStationButton;
    @BindView(R.id.send_station_button)
    Button sendStationButton;


    DatabaseReference dtbRef;
    FirebaseDatabase firebaseDatabase;
    private FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth auth;
    private static final int myRequestCode = 1;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.addbusstation_layout, container, false);
        ButterKnife.bind(this, v);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    inputLatitude.setText(location.getLatitude()+"");
                    inputLongitde.setText(location.getLongitude()+"");
                }
            });
        }

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if(auth != null){
            if(auth.getCurrentUser() != null){
                if (SharedPrefs.getsInstance(getActivity()).getAdmin()){
                    textView.setText(R.string.new_bus_station_admin_text);
                    newStationButton.setVisibility(View.VISIBLE);
                }else{
                    textView.setText(R.string.new_bus_station_text);
                }
            }else{
                notLogedInUILoad();
            }
        }

        return v;
    }

    @OnClick(R.id.new_stations_button)
    public void openList(){
        Fragment fragment = new ListOfNewStations();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.customfragment_layout, fragment, ListOfNewStations.class.getSimpleName()).commit();
    }


    private void insertNewStation(){
        if (checkFields()){
            if(SharedPrefs.getsInstance(getActivity()).getAdmin()){
                dtbRef = firebaseDatabase.getReference("busStations");
                Toast.makeText(getActivity(), getString(R.string.upload_alert_uploaded), Toast.LENGTH_LONG).show();
            }else{
                dtbRef = firebaseDatabase.getReference("newStations");
                String emailSubject = "New BusStation!";
                String emailBody = "The station name : "+inputStationName.getText().toString()+";\n"
                        +"The latitude : "+inputLatitude.getText().toString()+";\n"
                        +"The longitude : "+inputLongitde.getText().toString()+";\n"
                        +"The buslanes : "+inputBuses.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?to="+administratorEmail+"&subject="+emailSubject+"&body="+emailBody);
                intent.setData(data);
                startActivityForResult(intent, myRequestCode);
            }
        }
        Utils.closeKeyboard(getActivity(), getView());
    }

    public boolean checkFields(){
        int problems=0;
        if (TextUtils.isEmpty(inputBuses.getText().toString())){
            Toast.makeText(getActivity(), getString(R.string.upload_form_buslane), Toast.LENGTH_LONG).show();
            problems++;
        }
        if (TextUtils.isEmpty(inputLatitude.getText().toString())){
            Toast.makeText(getActivity(), getString(R.string.upload_form_latitude), Toast.LENGTH_LONG).show();
            problems++;
        }
        if (TextUtils.isEmpty(inputLongitde.getText().toString())){
            Toast.makeText(getActivity(), getString(R.string.upload_form_longitude), Toast.LENGTH_LONG).show();
            problems++;
        }
        if (TextUtils.isEmpty(inputStationName.getText().toString())){
            Toast.makeText(getActivity(), getString(R.string.upload_form_station), Toast.LENGTH_LONG).show();
            problems++;
        }
        if (problems > 0){
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == myRequestCode){
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme_NoActionBar));
            if (resultCode == RESULT_OK){
                builder.setMessage(getString(R.string.upload_mail_failed));
            }
            if(resultCode == 0){
                builder.setMessage(getString(R.string.upload_mail_sent));
                Toast.makeText(getActivity(), getString(R.string.upload_alert_uploaded), Toast.LENGTH_LONG).show();
            }
                builder.setNeutralButton(getString(R.string.alert_button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Fragment fragment = new MainFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.customfragment_layout, fragment, MainFragment.class.getSimpleName()).commit();
                    }
                });
                builder.show();
            Utils.closeKeyboard(getActivity(), getView());
        }
    }

    @OnClick(R.id.send_station_button)
    public void uploadStation(){

        DialogBox alert = new DialogBox(getActivity(), new DialogBox.OnAlertDialgoClickCallback() {

            @Override
            public void setOnClickMethod(android.app.AlertDialog.Builder builder) {
                builder.setPositiveButton(getString(R.string.alert_button_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        insertNewStation();
                        Location origo = new Location("");
                        origo.setLongitude(0);
                        origo.setLatitude(0);
                        Location location = new Location("");
                        location.setLatitude(Double.parseDouble(inputLatitude.getText().toString()));
                        location.setLongitude(Double.parseDouble(inputLongitde.getText().toString()));
                        BusStation station = new BusStation();
                        station.setName(inputStationName.getText().toString());
                        station.setBuses(inputBuses.getText().toString());
                        station.setLatitude(location.getLatitude()+"");
                        station.setLongitude(location.getLongitude()+"");
                        int s = (int) location.distanceTo(origo);
                        dtbRef.child(s+"").setValue(station);
                    }
                });
            }
        }, new DialogBox.OnAlertDialgoClickCallback() {

            @Override
            public void setOnClickMethod(android.app.AlertDialog.Builder builder) {
                builder.setNegativeButton(getString(R.string.alert_button_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        alert.setMessage(getString(R.string.upload_data_alert_question));
        alert.setTitle(getString(R.string.upload_data_alert_title));
        alert.showDialog();


    }

    private void notLogedInUILoad(){
        inputBuses.setVisibility(View.GONE);
        inputLongitde.setVisibility(View.GONE);
        inputLatitude.setVisibility(View.GONE);
        inputStationName.setVisibility(View.GONE);
        textView.setText(getString(R.string.upload_form_signin));
        sendStationButton.setText(getString(R.string.signIn));
        sendStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs.getsInstance(getActivity()).setCalledFromAddBusStationActivity(true);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("calledFrom", AddBusStation.class.getSimpleName());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
