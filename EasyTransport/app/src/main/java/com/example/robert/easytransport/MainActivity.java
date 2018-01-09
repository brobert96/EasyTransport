package com.example.robert.easytransport;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.robert.easytransport.activities.LoginActivity;
import com.example.robert.easytransport.data.SharedPrefs;
import com.example.robert.easytransport.fragments.AddBusStation;
import com.example.robert.easytransport.fragments.MainFragment;
import com.example.robert.easytransport.fragments.ProfileFragment;
import com.example.robert.easytransport.fragments.SearchFragment;
import com.example.robert.easytransport.services.GPSTracker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.customfragment_layout)
    FrameLayout frameLayout;

    LocationManager locat;
    FirebaseAuth auth;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            FirebaseDatabase fireData = FirebaseDatabase.getInstance();
            final DatabaseReference dataRef = fireData.getReference("admins");
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        if(auth.getCurrentUser().getUid().equals(postSnapshot.getValue().toString())){
                            SharedPrefs.getsInstance(getApplicationContext()).setAdmin(true);
                        }else{
                            SharedPrefs.getsInstance(getApplicationContext()).setAdmin(false);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        ButterKnife.bind(this);



            Fragment fragment = new MainFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.customfragment_layout, fragment, "mainFragment");
            transaction.commit();


        setSupportActionBar(toolbar);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment_t = (Fragment) getFragmentManager().findFragmentByTag(MainFragment.class.getSimpleName());
            if(fragment_t != null && fragment_t.isVisible()){
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            Fragment visibleFragment =(Fragment) getFragmentManager().findFragmentByTag(ProfileFragment.class.getSimpleName());
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if(visibleFragment != null){
                if(visibleFragment.isVisible()){
                    visibleFragment.onDestroy();
                    transaction.remove(visibleFragment);
                }
            }
            Fragment fragment = new MainFragment();
            transaction.replace(R.id.customfragment_layout, fragment, MainFragment.class.getSimpleName()).commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment_t;

        if (id == R.id.nav_camera) {
            if(auth.getCurrentUser() == null){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }else {
                Fragment fragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.customfragment_layout, fragment, ProfileFragment.class.getSimpleName()).commit();
            }

        } else if (id == R.id.nav_gallery) {
            fragment_t = (Fragment) getFragmentManager().findFragmentByTag(AddBusStation.class.getSimpleName());
            if(fragment_t != null && fragment_t.isVisible()){
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }else {
                Fragment fragment = new AddBusStation();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.customfragment_layout, fragment, AddBusStation.class.getSimpleName()).commit();
            }


        } else if (id == R.id.nav_slideshow) {
            fragment_t = (Fragment) getFragmentManager().findFragmentByTag(ProfileFragment.class.getSimpleName());
            if(fragment_t != null && fragment_t.isVisible()){
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }else {
                Fragment fragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.customfragment_layout, fragment, ProfileFragment.class.getSimpleName()).commit();
            }


        } else if (id == R.id.nav_manage) {
            fragment_t = (Fragment) getFragmentManager().findFragmentByTag(MainFragment.class.getSimpleName());
            if(fragment_t != null && fragment_t.isVisible()){
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }else {
                Fragment fragment = new MainFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.customfragment_layout, fragment, MainFragment.class.getSimpleName()).commit();
            }


        }else if(id == R.id.nav_search){
            fragment_t = (Fragment) getFragmentManager().findFragmentByTag(SearchFragment.class.getSimpleName());
            if(fragment_t != null && fragment_t.isVisible()){
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }else {
                Fragment fragment = new SearchFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.customfragment_layout, fragment, SearchFragment.class.getSimpleName()).commit();
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        locat = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locat.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locat.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            @SuppressLint("RestrictedApi") AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme_NoActionBar));
            builder.setMessage("Turn on location, please!");
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            locat = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (!locat.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locat.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                @SuppressLint("RestrictedApi") AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AppTheme_NoActionBar));
                builder.setMessage("Turn on location, please!");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            stopService(new Intent(getApplicationContext(), GPSTracker.class));
    }

}
