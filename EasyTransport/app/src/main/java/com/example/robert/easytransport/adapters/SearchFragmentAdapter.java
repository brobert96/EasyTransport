package com.example.robert.easytransport.adapters;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.robert.easytransport.MainActivity;
import com.example.robert.easytransport.R;
import com.example.robert.easytransport.Utils.Utils;
import com.example.robert.easytransport.fragments.MainFragment;
import com.example.robert.easytransport.models.BusStation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Robert on 12/13/2017.
 */

public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.ViewHolder> {

    ArrayList<BusStation> result = new ArrayList<>();
    Context context;

    public SearchFragmentAdapter(ArrayList<BusStation> result, Context context) {
        this.result = result;
        this.context = context;
    }

    @Override
    public SearchFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stop_item_view, parent, false);
        this.context = view.getContext();
        return new SearchFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchFragmentAdapter.ViewHolder holder, final int position) {
        holder.textView.setText(result.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainFragment();
                FragmentTransaction transaction = ((MainActivity)context).getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("busStop", result.get(position).getID());
                fragment.setArguments(bundle);
                transaction.replace(R.id.customfragment_layout, fragment, "mainFragment").commit();
                Utils.closeKeyboard(context, holder.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.busLaneText)
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
