package com.example.islandmark;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.islandmark.model.LandmarkDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(@NonNull final CardViewHolder cardViewHolder, final int i) {
        LandmarkDetails temp = landmarkDetailsList.get(i);
        cardViewHolder.tempTitle.setText(temp.name + " ");
        cardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //use position value  to get clicked data from list
                LandmarkDetails temp = landmarkDetailsList.get(i);
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

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child(temp.getlinkURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri.toString())
                        .fit()
                        .centerCrop()
                        .into(cardViewHolder.imageView);
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
        ImageView imageView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tempTitle = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.imageView2);
        }

    }
}
