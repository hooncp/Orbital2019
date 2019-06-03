package com.example.islandmark;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.islandmark.model.LandmarkDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
        View view = inflater.inflate(R.layout.fragment_landmark, container, false);
        landmarkDetailsAdapter = new LandmarkDetailsAdapter(getContext(), landmarkDetailsList);
        listView = view.findViewById(R.id.landmark_details_list_view);
        listView.setAdapter(landmarkDetailsAdapter);
        loadUserData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                LandmarkDetails landmarkDetails = (LandmarkDetails)parent.getItemAtPosition(position);
                Bundle args = new Bundle();
                args.putString("docID", landmarkDetails.getDocumentID());
                LandmarkDetailsFragment newFragment = new LandmarkDetailsFragment();
                newFragment.setArguments(args);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFragment)
                        .commit();
                //Snackbar.make(view, "moving to fragment details", Snackbar.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public boolean displaySelectedScreen(Fragment fragment) {
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
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

    private void loadUserData() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(LandmarkDetails.landmarkDetailsKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        GeoPoint location = (GeoPoint)document.get(LandmarkDetails.locationKey);
                        String documentID = (String) document.getId();
                        LandmarkDetails details = new LandmarkDetails(description, name, location,documentID);
                        landmarkDetailsAdapter.add(details);
                    }
                }
                landmarkDetailsAdapter.notifyDataSetChanged();
            }
        });
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
