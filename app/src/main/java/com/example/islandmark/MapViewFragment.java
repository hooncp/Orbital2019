package com.example.islandmark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.islandmark.model.LandmarkDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private GoogleMap map;
    LandmarkDetails landmark;

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            landmark = bundle.getParcelable("LANDMARKOBJ");
        }
        getActivity().setTitle(getString(R.string.Map));
    }

    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.Map));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng currentlocation = new LatLng(LandmarkDetails.currentlat, LandmarkDetails.currentlong);
        Drawable circleDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.current_location, null);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        googleMap.addMarker(new MarkerOptions()
                .position(currentlocation)
                .title(getString(R.string.current))
                .icon(markerIcon));
        MainActivity main = (MainActivity) getActivity();
        List<LandmarkDetails> landmarkDetailsList = main.getList();
        String name2;
        for (LandmarkDetails landmarktemp : landmarkDetailsList) {
            double longitude = landmarktemp.location.getLongitude();
            double latitude = landmarktemp.location.getLatitude();
            LatLng temp = new LatLng(latitude, longitude);

            if (MainActivity.language.equals("en")){
                name2 = landmarktemp.name;
            } else {
                name2 = landmarktemp.namezh;
            }

            if (landmarktemp.type.equals("Recreational")) {
                Drawable recreaDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.recreational, null);
                BitmapDescriptor markerIcon1 = getMarkerIconFromDrawable(recreaDrawable);
                googleMap.addMarker(new MarkerOptions().position(temp)
                        .title(name2).icon(markerIcon1));
            } else if (landmarktemp.type.equals("Cultural")) {
                Drawable recreaDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.cult, null);
                BitmapDescriptor markerIcon1 = getMarkerIconFromDrawable(recreaDrawable);
                googleMap.addMarker(new MarkerOptions().position(temp)
                        .title(name2).icon(markerIcon1));
            } else if (landmarktemp.type.equals("Historical")) {
                googleMap.addMarker(new MarkerOptions().position(temp)
                        .title(name2));
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentlocation));
        if (landmark == null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 13.0f));
        } else {
            double longitude = landmark.location.getLongitude();
            double latitude = landmark.location.getLatitude();
            LatLng temp = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 15.0f));
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initializeMap() {
        if (map == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
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
