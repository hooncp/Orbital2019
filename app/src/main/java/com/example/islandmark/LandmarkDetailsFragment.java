package com.example.islandmark;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.islandmark.model.LandmarkDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandmarkDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LandmarkDetailsFragment newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandmarkDetailsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    Button locateBtn, startBtn, addBtn, reviewBtn;
    LandmarkDetails landmark;
    TextView tv4;
    TextView tv;
    TextView tv3;
    TextView tv2;
    TextView tv5;
    ImageView imageView;
    private FirebaseAuth mAuth;
    private DatabaseReference dataref;
    private FirebaseDatabase database;


    public LandmarkDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            landmark = bundle.getParcelable("LANDMARKOBJ");
        }
        getActivity().setTitle(landmark.name);
    }

    public void onResume() {
        super.onResume();
        getActivity().setTitle(landmark.name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_landmark_details, container, false);
        locateBtn = view.findViewById(R.id.locateBtn);
        locateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putParcelable("LANDMARKOBJ", landmark);
                MapViewFragment mapFragment = new MapViewFragment();
                mapFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, mapFragment)
                        .commit();
            }
        });
        startBtn = view.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                activity.checkLocation();
                if (landmark.getDistance() < 10) {
                    // can change to pop up instead.
                    Snackbar.make(view, "moving to AR mode", Snackbar.LENGTH_LONG).show();
                    // mark landmark as visited after this button is pressed

                    mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser()!= null){
                        database = FirebaseDatabase.getInstance();
                        dataref = database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Locations");

                        HashMap locationMap = new HashMap();
                        locationMap.put(landmark.name,"1");

                        dataref.updateChildren(locationMap);
                    }
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), Ar_activity.class);
                    getActivity().startActivity(intent);

                } else {
                    Snackbar.make(view, "You are not there yet", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        addBtn = view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Landmark added to package!",Toast.LENGTH_SHORT).show();
            }
        });

        reviewBtn = view.findViewById(R.id.reviewBtn);
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),ReviewsActivity.class);
                intent.putExtra("landmark",landmark);
                getActivity().startActivity(intent);
            }
        });
        tv4 = view.findViewById(R.id.textView4);
        tv4.setText("Distance : " + landmark.getDistance() + " m away");
        tv = view.findViewById(R.id.textView);
        tv.setText(landmark.name);
        tv3 = view.findViewById(R.id.textView3);
        String test = landmark.descriptionlong.replaceAll("newline", "\n");
        tv3.setText(test);
        tv2 = view.findViewById(R.id.textView2);
        tv2.setText("Type: " + landmark.type);
        tv5 = view.findViewById(R.id.textView5);
        tv5.setText("Duration: Roughly " + landmark.timespent + "hrs");
        imageView = view.findViewById(R.id.imageView);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child(landmark.getlinkURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri.toString())
                        .fit()
                        .centerCrop()
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Snackbar.make(view, exception.toString(), Snackbar.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
