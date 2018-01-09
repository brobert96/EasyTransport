package com.example.robert.easytransport.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.adapters.MostlyRecyclerAdapter;
import com.example.robert.easytransport.data.SharedPrefs;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Robert on 9/29/2017.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    @BindView(R.id.UsName)
    TextView textView;
    @BindView(R.id.visited_text)
    TextView visited_text;
    @BindView(R.id.UsPic)
    CircleImageView imageView;
    @BindView(R.id.rview)
    RecyclerView recyclerView;

    MostlyRecyclerAdapter adapter;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    FirebaseDatabase firebaseDatabase;
    GeoDataClient client;
    ArrayList<Pair<Place, ByteArrayOutputStream>> placePhotoPair;
    ValueEventListener valueEventListener;
    String authID;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.profilefragment_layout, container, false);

        ButterKnife.bind(this, v);

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        placePhotoPair = new ArrayList<>();

        dbRef = firebaseDatabase.getReference("userQueries");
        client = Places.getGeoDataClient(getActivity(), null);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), lm.getOrientation());
        adapter = new MostlyRecyclerAdapter(placePhotoPair);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(lm);

        authID = null;

        if (auth != null) {
            if (auth.getCurrentUser() != null) {
                authID = auth.getUid();
                textView.setText(auth.getCurrentUser().getEmail());
                textView.setTextSize(20);
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        placePhotoPair.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            OnCompleteListener onCompleteListenerPlaceBufferResponse = new OnCompleteListener<PlaceBufferResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                    if (task.isSuccessful()) {
                                        final Place p = task.getResult().get(0);
                                        Bitmap b = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.placeholder);
                                        ByteArrayOutputStream s = new ByteArrayOutputStream();
                                        b.compress(Bitmap.CompressFormat.JPEG, 100, s);
                                        Pair<Place, ByteArrayOutputStream> pair = new Pair<Place, ByteArrayOutputStream>(p, s);
                                        placePhotoPair.add(pair);
                                        OnCompleteListener onCompleteListenerPlacePhotoMetadataResponse = new OnCompleteListener<PlacePhotoMetadataResponse>() {
                                            @Override
                                            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                                                if (task.isSuccessful()) {
                                                    PlacePhotoMetadataResponse photos = task.getResult();
                                                    PlacePhotoMetadataBuffer photoMetadatas = photos.getPhotoMetadata();
                                                    Task<PlacePhotoResponse> photoResponseTask = null;
                                                    if (photoMetadatas.getCount() > 0) {
                                                        PlacePhotoMetadata placePhotoMetadata = photoMetadatas.get(0);
                                                        photoResponseTask = client.getPhoto(placePhotoMetadata);
                                                    }
                                                    if (photoResponseTask != null) {
                                                        OnCompleteListener onCompleteListenerPlacePhotoResponse = new OnCompleteListener<PlacePhotoResponse>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                                                if (task.isSuccessful()) {
                                                                    Bitmap photo;
                                                                    ByteArrayOutputStream outputStream;
                                                                    photo = task.getResult().getBitmap();
                                                                    outputStream = null;
                                                                    if (photo != null) {
                                                                        outputStream = new ByteArrayOutputStream();
                                                                        photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                                                        for (int i = 0; i < placePhotoPair.size(); i++) {
                                                                            Log.d(TAG, placePhotoPair.get(i).first.getName().toString() + " Ciklus");
                                                                            if (placePhotoPair.get(i).first.getName().equals(p.getName())) {
                                                                                Log.d(TAG, placePhotoPair.get(i).first.getName().toString() + " ciklus if");
                                                                                placePhotoPair.add(i, new Pair<Place, ByteArrayOutputStream>(p, outputStream));
                                                                                placePhotoPair.remove(i + 1);
                                                                            }
                                                                        }
                                                                    }
                                                                } else {
                                                                    Log.d(TAG, "No photos available");
                                                                }
                                                                Log.d(TAG, placePhotoPair.size() + "");
                                                                updateRecyclerView(placePhotoPair);
                                                            }

                                                        };
                                                        photoResponseTask.addOnCompleteListener(onCompleteListenerPlacePhotoResponse);
                                                    }
                                                }
                                            }
                                        };
                                        client.getPlacePhotos(p.getId()).addOnCompleteListener(onCompleteListenerPlacePhotoMetadataResponse);
                                    } else {
                                        Log.d(TAG, "place not found");
                                    }
                                }
                            };
                            client.getPlaceById(postSnapshot.getValue().toString()).addOnCompleteListener(onCompleteListenerPlaceBufferResponse);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            }

        }
        return v;
    }

    @OnClick(R.id.passwchange)
    public void changePassword(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_update_password, null);
        builder.setView(v)
                .setPositiveButton(getString(R.string.change_password_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText passw = (EditText) v.findViewById(R.id.password);
                        EditText passwconf = (EditText) v.findViewById(R.id.passwordconfirm);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (!passw.getText().toString().equals(passwconf.getText().toString())){
                            Toast.makeText(getActivity(), getString(R.string.incorrect_password), Toast.LENGTH_LONG);
                        }
                        user.updatePassword(passw.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getActivity(), getString(R.string.password_updated), Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(getActivity(), getString(R.string.password_update_failed), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
    @OnClick(R.id.discon)
    public void disconnect(){
        auth.signOut();
        Fragment fragment = new MainFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.customfragment_layout, fragment, MainFragment.class.getSimpleName()).addToBackStack(MainFragment.class.getSimpleName()).commit();
        SharedPrefs.getsInstance(getActivity()).setAdmin(false);
    }


    private void updateRecyclerView(final ArrayList<Pair<Place, ByteArrayOutputStream>> placePhotoPair){
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.setItem(placePhotoPair);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
            if(authID != null){
                dbRef.child(authID).removeEventListener(valueEventListener);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(authID != null){
            dbRef.child(auth.getUid()).addValueEventListener(valueEventListener);
        }
    }
}
