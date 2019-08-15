package com.example.islandmark;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.islandmark.model.LandmarkDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    List<LandmarkDetails> landmarkDetailsList = new ArrayList<>();
    HorizontalRecycleViewAdapter adapter;

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference dataref;
    private FirebaseDatabase database;
    private TextView itineraryView;
    private TextView welcomeText;

    private ListView packageList;
    private ArrayList<String> arrayListlm = new ArrayList<>();
    ArrayAdapter<String> arrayAdapterlm;

    String name;
    private String userid;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.Home_Screen));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.Home_Screen));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        MainActivity main = (MainActivity) getActivity();
//        landmarkDetailsList = main.getList();

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycle);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        view.findViewById(R.id.welcome).setVisibility(View.GONE);
        view.findViewById(R.id.textView7).setVisibility(View.GONE);
        itineraryView = view.findViewById(R.id.textView9);
        packageList = view.findViewById(R.id.packageList);

        if (mAuth.getCurrentUser()!=null)
        {
            userid = mUser.getUid();
            database.getReference().child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Toast.makeText(getContext(),"Please setup your account to unlock more functions",Toast.LENGTH_SHORT).show();
                        Intent setupIntent = new Intent(getContext(), SetupActivity.class);
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        welcomeText = view.findViewById(R.id.welcome);
        updateName();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateName();
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        loadRecommendedData();
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new HorizontalRecycleViewAdapter(landmarkDetailsList, getContext());
        recyclerView.setAdapter(adapter);
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

    public void updateName(){

        if (mUser!=null){
            dataref = database.getReference().child("Users").child(userid).child("fullname");
            dataref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue(String.class);

                    String text1 = getString(R.string.Welcome);
                    welcomeText.setText(text1 + name + "!");
                    String text2 = getString(R.string.itinerary);
                    itineraryView.setText(name+ text2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            dataref = database.getReference().child("Users").child(userid).child("Package");
            arrayAdapterlm = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayListlm);

            packageList.setAdapter(arrayAdapterlm);

            dataref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String lmdistance = dataSnapshot.getValue().toString();
                    String lmname = dataSnapshot.getKey();
                    String info = lmname + " : " + lmdistance+"m away";

                    if(!arrayListlm.contains(info)) {
                        arrayListlm.add(info);
                        arrayAdapterlm.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else{
            welcomeText.setText(getString(R.string.Welcome));
        }

    }

    private void loadRecommendedData() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(LandmarkDetails.landmarkDetailsKey).orderBy(LandmarkDetails.nameKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // clean up the list to prevent double copies
                landmarkDetailsList.removeAll(landmarkDetailsList);
                int count = 0;
                for (DocumentSnapshot document : documents) {

                    if (document.contains(LandmarkDetails.descriptionKey) && document.contains(LandmarkDetails.locationKey)
                            && document.contains(LandmarkDetails.nameKey)&& count < 5) {

                        String description = (String) document.get(LandmarkDetails.descriptionKey);
                        String name = (String) document.get(LandmarkDetails.nameKey);
                        GeoPoint location = (GeoPoint)document.get(LandmarkDetails.locationKey);
                        String documentID = (String) document.getId();
                        String timespent = (String)document.get(LandmarkDetails.timespentkey);
                        String descriptionlong = (String) document.get(LandmarkDetails.descriptionlongKey);
                        String type = (String) document.get(LandmarkDetails.typeKey);
                        String namezh = (String) document.get("namezh");
                        LandmarkDetails details = new LandmarkDetails(description, name, location,documentID,descriptionlong,timespent,type,namezh);
                        landmarkDetailsList.add(details);
                        count++;
                    }
                }
                adapter.notifyDataSetChanged();
                getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                getView().findViewById(R.id.welcome).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.textView7).setVisibility(View.VISIBLE);
            }
        });
    }
}
