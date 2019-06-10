package com.example.islandmark;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CardViewHolder extends RecyclerView.ViewHolder {
    public TextView tempTitle;

    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        tempTitle = itemView.findViewById(R.id.title);
    }

}
