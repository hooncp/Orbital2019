package com.example.islandmark;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.islandmark.model.LandmarkDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandmarkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LandmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandmarkFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private List<LandmarkDetails> landmarkDetailsList = new ArrayList<>();
    private ListView listView;
    private LandmarkDetailsAdapter landmarkDetailsAdapter;
    EditText editText;

    public LandmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Landmarks");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_landmark, container, false);
        editText = view.findViewById(R.id.search_edit_text);
        landmarkDetailsAdapter = new LandmarkDetailsAdapter(getContext(), landmarkDetailsList);
        listView = view.findViewById(R.id.landmark_details_list_view);
        listView.setAdapter(landmarkDetailsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                LandmarkDetails landmarkDetails = (LandmarkDetails) parent.getItemAtPosition(position);
                //TODO after implementing Parcelable, just pass the object through, no need to read the database again
                Bundle args = new Bundle();
                args.putParcelable("LANDMARKOBJ", landmarkDetails);
                LandmarkDetailsFragment newFragment = new LandmarkDetailsFragment();
                newFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFragment)
                        .commit();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {
                    searchUserData(s.toString());
                } else {
                    loadLandmarkDetails();
                }
            }
        });
        loadLandmarkDetails();
//        TextView e    mptyText = (TextView)view.findViewById(android.R.id.empty);
//        listView.setEmptyView(emptyText);
        return view;
    }

    private void loadLandmarkDetails() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(LandmarkDetails.landmarkDetailsKey).orderBy(LandmarkDetails.nameKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // clean up the list to prevent double copies
                landmarkDetailsList.removeAll(landmarkDetailsList);

                for (DocumentSnapshot document : documents) {

                    if (document.contains(LandmarkDetails.descriptionKey) && document.contains(LandmarkDetails.locationKey)
                            && document.contains(LandmarkDetails.nameKey)) {

                        String description = (String) document.get(LandmarkDetails.descriptionKey);
                        String name = (String) document.get(LandmarkDetails.nameKey);
                        GeoPoint location = (GeoPoint) document.get(LandmarkDetails.locationKey);
                        String documentID = (String) document.getId();
                        LandmarkDetails details = new LandmarkDetails(description, name, location, documentID);
                        landmarkDetailsList.add(details);
                    }
                }
                landmarkDetailsAdapter.notifyDataSetChanged();
                getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }

    private void searchUserData(String filter) {


        FirebaseFirestore fs = FirebaseFirestore.getInstance();


        fs.collection(LandmarkDetails.landmarkDetailsKey).whereGreaterThan(LandmarkDetails.nameKey, filter)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // clean up the list to prevent double copies
                landmarkDetailsList.removeAll(landmarkDetailsList);

                for (DocumentSnapshot document : documents) {

                    if (document.contains(LandmarkDetails.descriptionKey) && document.contains(LandmarkDetails.locationKey)
                            && document.contains(LandmarkDetails.nameKey)) {

                        String description = (String) document.get(LandmarkDetails.descriptionKey);
                        String name = (String) document.get(LandmarkDetails.nameKey);
                        GeoPoint location = (GeoPoint) document.get(LandmarkDetails.locationKey);
                        String documentID = (String) document.getId();
                        LandmarkDetails details = new LandmarkDetails(description, name, location, documentID);
                        landmarkDetailsList.add(details);
                    }
                }
                landmarkDetailsAdapter.notifyDataSetChanged();
                getView().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
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
