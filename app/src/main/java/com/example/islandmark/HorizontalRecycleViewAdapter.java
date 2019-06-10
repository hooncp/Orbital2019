package com.example.islandmark;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.islandmark.model.LandmarkDetails;

import java.util.List;

public class HorizontalRecycleViewAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private List<LandmarkDetails> landmarkDetailsList;

    public HorizontalRecycleViewAdapter(List<LandmarkDetails> landmarkDetailsList){
        this.landmarkDetailsList = landmarkDetailsList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        LandmarkDetails temp = landmarkDetailsList.get(i);
        cardViewHolder.tempTitle.setText(temp.name + " ");
    }

    @Override
    public int getItemCount() {
        return landmarkDetailsList.size();
    }
}
