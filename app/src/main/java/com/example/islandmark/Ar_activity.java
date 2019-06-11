package com.example.islandmark;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.ar.sceneform.*;
import com.google.ar.sceneform.ux.ArFragment;

public class Ar_activity extends Fragment {

    private ArFragment arFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arFragment = (ArFragment)getFragmentManager()
                .findFragmentById(R.id.sceneform_fragment);

        ArSceneView arView = arFragment.getArSceneView();
        Scene scene = arView.getScene();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ar, container, false);
        return rootView;
    }
}
