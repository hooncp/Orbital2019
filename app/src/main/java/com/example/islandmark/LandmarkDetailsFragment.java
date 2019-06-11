package com.example.islandmark;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.islandmark.model.LandmarkDetails;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandmarkDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LandmarkDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandmarkDetailsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    Button locateBtn;
    Button startBtn;
    LandmarkDetails landmark;
    TextView tv4;
    TextView tv;
    TextView tv3;
    TextView tv2;
    TextView tv5;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landmark_details, container, false);
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
                if (landmark.getDistance() <= 10) {
                    // can change to pop up instead.
                    Snackbar.make(view, "moving to AR mode", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, "You are not there yet", Snackbar.LENGTH_LONG).show();
                }
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
