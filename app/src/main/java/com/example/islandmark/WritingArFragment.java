package com.example.islandmark;

import android.Manifest;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.core.AugmentedImageDatabase;

import java.io.IOException;
import java.io.InputStream;

public class WritingArFragment extends ArFragment {
    //tag
    private static final String TAG = "AugmentedImageFragment";

    //sample database
    private static final String SAMPLE_IMAGE_DATABASE = "sample_database.imgdb";

    //image name, can be used to either generate database at runtime/ test augmented image
    private static final String DEFAULT_IMAGE_NAME = "default.jpg";

    //select if you want to use a pre-generated database or generate at runtime
    private static final boolean USE_SINGLE_IMAGE = false;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Turn off the plane discovery since we're only looking for images
        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);
        getArSceneView().getPlaneRenderer().setEnabled(false);
        return view;
    }

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = super.getSessionConfiguration(session);
        if (!setupAugmentedImageDatabase(config, session)) {
            /*SnackbarHelper.getInstance()
                    .showError(getActivity(), "Could not setup augmented image database");*/
        }
        return config;
    }

    private boolean setupAugmentedImageDatabase(Config config, Session session) {
        AugmentedImageDatabase augmentedImageDatabase;

        AssetManager assetManager = getContext() != null ? getContext().getAssets() : null;
        if (assetManager == null) {
            Log.e(TAG, "Context is null, cannot intitialize image database.");
            return false;
        }

        if (USE_SINGLE_IMAGE) {
            Bitmap augmentedImageBitmap = loadAugmentedImageBitmap(assetManager);
            if (augmentedImageBitmap == null) {
                return false;
            }
            //generate img database during runtime (saves space) or

            augmentedImageDatabase = new AugmentedImageDatabase(session);
            augmentedImageDatabase.addImage(DEFAULT_IMAGE_NAME, augmentedImageBitmap);
        } else {
            //generate beforehand (this method saves time)

            try (InputStream is = getContext().getAssets().open(SAMPLE_IMAGE_DATABASE)) {
                augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
            } catch (IOException e) {
                Log.e(TAG, "IO exception loading augmented image database.", e);
                return false;
            }
        }

        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;

    }

    private Bitmap loadAugmentedImageBitmap(AssetManager assetManager) {
        try (InputStream is = assetManager.open(DEFAULT_IMAGE_NAME)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e);
        }
        return null;
    }
}
