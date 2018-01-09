package com.example.robert.easytransport.adapters;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.robert.easytransport.MainActivity;
import com.example.robert.easytransport.R;
import com.example.robert.easytransport.fragments.MainFragment;
import com.google.android.gms.location.places.Place;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Robert on 10/19/2017.
 */

public class MostlyRecyclerAdapter extends RecyclerView.Adapter<MostlyRecyclerAdapter.ViewHolder> {

    private static final String TAG = MostlyRecyclerAdapter.class.getSimpleName();
    Context context;
    ArrayList<Pair<Place, ByteArrayOutputStream>> placePhotoPair;

    public MostlyRecyclerAdapter(ArrayList<Pair<Place, ByteArrayOutputStream>> placePhotoPair){
        this.placePhotoPair = placePhotoPair;
    }

    @Override
    public MostlyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rviewitem, parent, false);
        this.context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MostlyRecyclerAdapter.ViewHolder holder, final int position) {

        final Place place = placePhotoPair.get(position).first;

        Glide.with(context)
                .load(placePhotoPair.get(position).second.toByteArray())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
        holder.textView.setText(place.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new MainFragment();
                    FragmentTransaction transaction = ((MainActivity)context).getFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("placeID", place.getId());
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.customfragment_layout, fragment, "mainFragment").commit();
                }
            });
    }

    @Override
    public int getItemCount() {
        return placePhotoPair.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.place_address)
        TextView textView;
        @BindView(R.id.place_image)
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setItem(ArrayList<Pair<Place, ByteArrayOutputStream>> placePhotoPair){
        this.placePhotoPair = placePhotoPair;
    }
}
