package com.example.islandmark;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.core.AugmentedImageDatabase;

public class WritingArFragment extends ArFragment {

    @Override
    public String[] getAdditionalPermissions() {
        String[] additionalPermissions = super.getAdditionalPermissions();
        int permissionLength = additionalPermissions != null ? additionalPermissions.length : 0;
        String[] permissions = new String[permissionLength + 1];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (permissionLength > 0) {
            System.arraycopy(additionalPermissions, 0, permissions, 1, additionalPermissions.length);
        }
        return permissions;
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        AugmentedImageDatabase augmentedImageDatabase = new AugmentedImageDatabase(session);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.labrador_battery);

        augmentedImageDatabase.addImage("image", bitmap);

        config.setAugmentedImageDatabase(augmentedImageDatabase);

        this.getArSceneView().setupSession(session);

        return config;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater,container,savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;
    }
}
