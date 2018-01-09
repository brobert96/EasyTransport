package com.example.robert.easytransport.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.fragments.MainFragment;
import com.example.robert.easytransport.models.ResultForAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Robert on 11/13/2017.
 */

public class BusStationResultAdapter extends RecyclerView.Adapter<BusStationResultAdapter.ViewHolder> {

    int position;
    ArrayList<ResultForAdapter> result;
    Context context;
    MainFragment mf;
    MainFragment.OnClickBuses callback;

    public BusStationResultAdapter(ArrayList<ResultForAdapter> result, MainFragment.OnClickBuses callback){
        this.callback = callback;
        this.result = result;
        this.mf = mf;
    }

    @Override
    public BusStationResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent, false);
        context=view.getContext();
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(BusStationResultAdapter.ViewHolder holder, final int position) {
        this.position = position;
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.start));
        builder.append(": ");
        builder.append(result.get(position).getStation1().getName());
        builder.append("\n");
        builder.append(context.getString(R.string.end));
        builder.append(": ");
        builder.append(result.get(position).getStation2().getName());
        builder.append("\n");
        builder.append(context.getString(R.string.lane));
        builder.append(": ");
        builder.append(result.get(position).getLane().getNumber());
        holder.textView1.setText(builder.toString());
        builder = new StringBuilder();
        if(position==0){
            builder.append(result.get(position).getDuration()/60);
            builder.append("min ");
            builder.append(result.get(position).getDuration()%60);
            builder.append("sec");
            builder.append("\nBest!");
            holder.textView2.setText(builder.toString());
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        }else {
            builder.append(result.get(position).getDuration()/60);
            builder.append("min ");
            builder.append(result.get(position).getDuration()%60);
            builder.append("sec");
            holder.textView2.setText(builder.toString());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.setMarkers(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.resultItem)
        TextView textView1;
        @BindView(R.id.duration_result)
        TextView textView2;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

}
