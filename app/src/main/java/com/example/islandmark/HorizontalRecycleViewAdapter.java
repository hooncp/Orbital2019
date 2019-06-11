package com.example.islandmark;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.islandmark.model.LandmarkDetails;

import java.util.List;

public class HorizontalRecycleViewAdapter extends RecyclerView.Adapter<HorizontalRecycleViewAdapter.CardViewHolder> {
    private List<LandmarkDetails> landmarkDetailsList;
    private Context context;

    public HorizontalRecycleViewAdapter(List<LandmarkDetails> landmarkDetailsList, Context context){
        this.landmarkDetailsList = landmarkDetailsList;
        this.context = context;
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
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, final int i) {
        LandmarkDetails temp = landmarkDetailsList.get(i);
        cardViewHolder.tempTitle.setText(temp.name + " ");
        cardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //use position value  to get clicked data from list
                LandmarkDetails temp = landmarkDetailsList.get(i);
                Snackbar.make(view,temp.name, Snackbar.LENGTH_LONG).show();

                Bundle args = new Bundle();
                args.putParcelable("LANDMARKOBJ", temp);
                LandmarkDetailsFragment newFragment = new LandmarkDetailsFragment();
                newFragment.setArguments(args);

                ((AppCompatActivity)context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFragment)
                        .commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return landmarkDetailsList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tempTitle;
        View view;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tempTitle = itemView.findViewById(R.id.title);
        }

    }
}
