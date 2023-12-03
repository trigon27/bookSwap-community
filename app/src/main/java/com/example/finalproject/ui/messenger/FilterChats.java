package com.example.finalproject.ui.messenger;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterChats extends Filter {
    private Adapterchats adapter;
    private ArrayList<Modelchats> filterList;

    public FilterChats(Adapterchats adapter, ArrayList<Modelchats> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraints) {
        FilterResults results=new FilterResults();
        if(constraints!=null && constraints.length() >=0 ){
            constraints=constraints.toString().toUpperCase();
            ArrayList<Modelchats> filteredModels=new ArrayList<>();

            for (int i=0; i<filterList.size(); i++) {

                if (filterList.get(i).getName().toUpperCase().contains(constraints)) {

                    filteredModels.add(filterList.get(i));
                }

                }
            results.count=filteredModels.size();
            results.values=filteredModels;
            }else {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        adapter.chatsArrayList=(ArrayList<Modelchats>) results.values;
        adapter.notifyDataSetChanged();

    }
}
