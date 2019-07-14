package com.example.islandmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.islandmark.model.LandmarkDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReviewsActivity extends AppCompatActivity {
    LandmarkDetails landmark;
    private ImageButton postReview;
    private EditText inputReview;
    private RecyclerView reviewsList;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference dataref;
    private DatabaseReference userref;

    private String userId;
    private String username;

    //spaghetti code lul

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        landmark = getIntent().getExtras().getParcelable("landmark");
        setTitle("Reviews for "+landmark.name);

        postReview = (ImageButton)findViewById(R.id.post_review);
        inputReview = (EditText)findViewById(R.id.review_input);
        reviewsList = (RecyclerView)findViewById(R.id.reviews);
        reviewsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        reviewsList.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser!=null){
            userId = mUser.getUid();
            userref = database.getInstance().getReference().child("Users").child(userId).child("username");
            userref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    username = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        database = FirebaseDatabase.getInstance();
        dataref = database.getReference().child("Reviews").child(landmark.name).child(userId);

        postReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //settling the two most common errors

                if (mUser!=null){
                    String review = inputReview.getText().toString();

                    if(review.length()!=0) {

                        HashMap reviewMap = new HashMap();
                        reviewMap.put("review", review);
                        reviewMap.put("username", username);

                        dataref.updateChildren(reviewMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    inputReview.setText("");
                                    Toast.makeText(ReviewsActivity.this, "Review posted successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ReviewsActivity.this, "Review failed to post.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(ReviewsActivity.this,"Field is still empty",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(ReviewsActivity.this,"Please sign in to comment",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Review> options =
                new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(database.getReference().child("Reviews").child(landmark.name),Review.class)
                .build();

        FirebaseRecyclerAdapter<Review,reviewsHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Review, reviewsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull reviewsHolder reviewsHolder, int i, @NonNull Review review)
            {
                reviewsHolder.userName.setText(review.getUsername());
                reviewsHolder.review.setText(review.getUser_review());

            }

            @NonNull
            @Override
            public reviewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
                reviewsHolder viewHolder = new reviewsHolder(view);
                return viewHolder;
            }
        };

        reviewsList.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    public static class reviewsHolder extends RecyclerView.ViewHolder
    {

        TextView userName, review;

        public reviewsHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.review_username);
            review = itemView.findViewById(R.id.review_message);
        }
    }

}
