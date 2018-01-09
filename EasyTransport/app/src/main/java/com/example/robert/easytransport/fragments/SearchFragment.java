package com.example.robert.easytransport.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.adapters.SearchFragmentAdapter;
import com.example.robert.easytransport.data.SharedPrefs;
import com.example.robert.easytransport.models.BusStation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment {

    @BindView(R.id.searchview)
    SearchView searchView;
    @BindView(R.id.searchRview)
    RecyclerView recyclerView;
    private static final String TAG = SearchFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, v);

        final ArrayList<BusStation> allStations = SharedPrefs.getsInstance(getActivity()).getStations();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<BusStation> results = new ArrayList<>();
                results.clear();
                for(BusStation bs : allStations){
                    if(bs.getName().toLowerCase().contains(query.toLowerCase())){
                        results.add(bs);
                    }
                }
                setRecyclerViewItems(results);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<BusStation> results2 = new ArrayList<>();
                results2.clear();
                for(BusStation bs : allStations){
                    if(bs.getName().toLowerCase().contains(newText.toLowerCase())){
                        results2.add(bs);
                    }
                }
                if(newText.equals("")){
                    results2.clear();
                }
                setRecyclerViewItems(results2);
                return false;
            }
        });

        return v;
    }

    public void setRecyclerViewItems(ArrayList<BusStation> result){
        SearchFragmentAdapter adapter;
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        adapter = new SearchFragmentAdapter(result, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(lm);
    }

}
