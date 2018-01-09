package com.example.robert.easytransport.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.fragments.ListOfNewStations;
import com.example.robert.easytransport.models.BusStation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Robert on 11/16/2017.
 */

public class NewStationAdapter extends RecyclerView.Adapter<NewStationAdapter.ViewHolder>{



    ArrayList<BusStation> newStations;
    ListOfNewStations.OnClickAcceptButtonCallback acceptCallback;
    ListOfNewStations.OnClickDeleteButtonCallback deleteCallback;

    public NewStationAdapter(ArrayList<BusStation> newStations, ListOfNewStations.OnClickDeleteButtonCallback deleteCallback, ListOfNewStations.OnClickAcceptButtonCallback acceptCallback) {
        this.newStations = newStations;
        this.acceptCallback = acceptCallback;
        this.deleteCallback = deleteCallback;
    }

    @Override
    public NewStationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_stations_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewStationAdapter.ViewHolder holder, final int position) {
        final BusStation stationOnPosition = newStations.get(position);

        StringBuilder builder = new StringBuilder();
        builder.append(stationOnPosition.getName());
        builder.append("\n");
        builder.append(stationOnPosition.getBuses());
        builder.append("\n");
        builder.append(stationOnPosition.getLatitude()+", "+stationOnPosition.getLongitude());
        holder.attributesText.setText(builder.toString());

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCallback.onDelete(stationOnPosition);
            }
        });
        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptCallback.onAccept(stationOnPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newStations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.decline)
        Button declineButton;
        @BindView(R.id.accept)
        Button acceptButton;
        @BindView(R.id.attributes)
        TextView attributesText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
