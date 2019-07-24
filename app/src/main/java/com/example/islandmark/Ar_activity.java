package com.example.islandmark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.Nullable;

import com.example.islandmark.model.CustomVisualizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.*;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Ar_activity extends AppCompatActivity implements View.OnClickListener {

    private WritingArFragment arFragment;
    private ModelRenderable bearRenderable,
                            elephantRenderable,
                            horseRenderable,
                            koalaRenderable,
                            lionRenderable,
                            reindeerRenderable,
                            videoRenderable,
                            volcanoRenderable,
                            eiffelRenderable,
                            merlionRenderable,
                            deadmemeRenderable;

    ImageView bear, elephant, horse, koala, lion, reindeer, volcano, eiffel, merlion, deadmeme;

    //for the future when we import more models
    View arrayView[];

    int selected = 1;

    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    private ExternalTexture externalTexture;
    private MediaPlayer mediaPlayer;
    private Scene scene;
    private Boolean detected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ar);
        arFragment = (WritingArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        arFragment.getTransformationSystem().setSelectionVisualizer(new CustomVisualizer());

        bear = findViewById(R.id.bear);
        elephant = findViewById(R.id.elephant);
        horse = findViewById(R.id.horse);
        koala = findViewById(R.id.koalabear);
        lion = findViewById(R.id.lion);
        reindeer = findViewById(R.id.reindeer);
        volcano = findViewById(R.id.volcano);
        eiffel = findViewById(R.id.eiffel);
        merlion = findViewById(R.id.merlion);
        deadmeme = findViewById(R.id.deadmeme);
        setArrayView();

        setClickListener();

        setUpModel();
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            createModel(anchorNode, selected);
        });

        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            arFragment.onUpdate(frameTime);
            onUpdate();
        });

        scene = arFragment.getArSceneView().getScene();
        scene.addOnUpdateListener(this::updateDetect);
        FloatingActionButton btnPhoto = findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(view -> takePhoto());

        externalTexture = new ExternalTexture();
        mediaPlayer = MediaPlayer.create(this,R.raw.test_video);
        mediaPlayer.setSurface(externalTexture.getSurface());

        mediaPlayer.setLooping(false);

        ModelRenderable
                .builder()
                .setSource(this,Uri.parse("video_screen.sfb"))
                .build()
                .thenAccept(modelRenderable ->
                {modelRenderable.getMaterial().setExternalTexture("videoTexture", externalTexture);
                modelRenderable.getMaterial().setFloat4("keyColor",
                        new Color(0.01843f,1f,0.098f));
                videoRenderable = modelRenderable;
                });


    }

    private void updateDetect(FrameTime frameTime) {
        if(detected)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> augmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage image: augmentedImages){

            if (image.getTrackingState()== TrackingState.TRACKING){

                if (image.getName().equals("image")){
                    detected = true;
                    playVideo(image.createAnchor(image.getCenterPose()),image.getExtentX(),image.getExtentZ());
                    break;
                }
                //add conditions here if want more video on image, rmb to create the bitmap for images
            }
        }
    }

    private void playVideo(Anchor anchor, float extentX, float extentZ) {
        mediaPlayer.start();

        AnchorNode anchorNode= new AnchorNode(anchor);

        externalTexture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(videoRenderable);
            externalTexture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX,1f,extentZ));

        scene.addChild(anchorNode);
    }

    private void onUpdate() {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = arFragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth()/2, vw.getHeight()/2);
    }


    private void setUpModel() {
        ModelRenderable.builder()
                .setSource(this,R.raw.bear)
                .build().thenAccept(renderable -> bearRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=
                                    Toast.makeText(getApplicationContext(),"unable to load bear model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this,R.raw.elephant)
                .build().thenAccept(renderable -> elephantRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load elephant model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.horse)
                .build().thenAccept(renderable -> horseRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load horse model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.koala_bear)
                .build().thenAccept(renderable -> koalaRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load koala bear model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );
        ModelRenderable.builder()
                .setSource(this,R.raw.lion)
                .build().thenAccept(renderable -> lionRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load lion model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.reindeer)
                .build().thenAccept(renderable -> reindeerRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load reindeer model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.volcano)
                .build().thenAccept(renderable -> volcanoRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load volcano model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.eiffel)
                .build().thenAccept(renderable -> eiffelRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load eiffel model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.maya2sketchfab)
                .build().thenAccept(renderable -> merlionRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load merlion model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.knuckles)
                .build().thenAccept(renderable -> deadmemeRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load knuckles model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

    }

    private void createModel(AnchorNode anchorNode, int selected) {
        if(selected == 1){
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.getScaleController().setMaxScale(10.0f);
            bear.getScaleController().setMinScale(0.01f);
            bear.setLocalScale(new Vector3(10f,10f,10f));

            bear.setParent(anchorNode);
            bear.setRenderable(bearRenderable);
            bear.select();

        }

        if(selected == 2){
            TransformableNode elephant = new TransformableNode(arFragment.getTransformationSystem());
            elephant.getScaleController().setMaxScale(10.0f);
            elephant.getScaleController().setMinScale(0.01f);
            elephant.setLocalScale(new Vector3(10f,10f,10f));

            elephant.setParent(anchorNode);
            elephant.setRenderable(elephantRenderable);
            elephant.select();
        }

        if(selected == 3){
            TransformableNode horse = new TransformableNode(arFragment.getTransformationSystem());
            horse.getScaleController().setMaxScale(10.0f);
            horse.getScaleController().setMinScale(0.01f);
            horse.setLocalScale(new Vector3(10f,10f,10f));

            horse.setParent(anchorNode);
            horse.setRenderable(horseRenderable);
            horse.select();
        }

        if(selected == 4){
            TransformableNode koala_bear = new TransformableNode(arFragment.getTransformationSystem());
            koala_bear.getScaleController().setMaxScale(10.0f);
            koala_bear.getScaleController().setMinScale(0.01f);
            koala_bear.setLocalScale(new Vector3(10f,10f,10f));

            koala_bear.setParent(anchorNode);
            koala_bear.setRenderable(koalaRenderable);
            koala_bear.select();
        }

        if(selected == 5){
            TransformableNode lion = new TransformableNode(arFragment.getTransformationSystem());
            lion.getScaleController().setMaxScale(10.0f);
            lion.getScaleController().setMinScale(0.01f);
            lion.setLocalScale(new Vector3(10f,10f,10f));

            lion.setParent(anchorNode);
            lion.setRenderable(lionRenderable);
            lion.select();
        }

        if(selected == 6){
            TransformableNode reindeer = new TransformableNode(arFragment.getTransformationSystem());
            reindeer.getScaleController().setMaxScale(10.0f);
            reindeer.getScaleController().setMinScale(0.01f);
            reindeer.setLocalScale(new Vector3(10f,10f,10f));

            reindeer.setParent(anchorNode);
            reindeer.setRenderable(reindeerRenderable);
            reindeer.select();
        }

        if(selected == 7) {
            TransformableNode volcano = new TransformableNode(arFragment.getTransformationSystem());
            volcano.getScaleController().setMaxScale(10.0f);
            volcano.getScaleController().setMinScale(0.01f);
            volcano.setLocalScale(new Vector3(1f, 1f, 1f));

            volcano.setParent(anchorNode);
            volcano.setRenderable(volcanoRenderable);
            volcano.select();
        }
        if(selected == 8) {
            TransformableNode eiffel = new TransformableNode(arFragment.getTransformationSystem());
            eiffel.getScaleController().setMaxScale(10.0f);
            eiffel.getScaleController().setMinScale(0.01f);
            eiffel.setLocalScale(new Vector3(1f, 1f, 1f));

            eiffel.setLocalRotation(Quaternion.axisAngle(new Vector3(1f,0,0),270));

            eiffel.setParent(anchorNode);
            eiffel.setRenderable(eiffelRenderable);
            eiffel.select();
        }
        if(selected == 9) {
            TransformableNode merlion = new TransformableNode(arFragment.getTransformationSystem());
            merlion.getScaleController().setMaxScale(10.0f);
            merlion.getScaleController().setMinScale(0.01f);
            merlion.setLocalScale(new Vector3(1f, 1f, 1f));

            merlion.setParent(anchorNode);
            merlion.setRenderable(merlionRenderable);
            merlion.select();
        }
        if(selected == 10) {
            TransformableNode deadmeme = new TransformableNode(arFragment.getTransformationSystem());
            deadmeme.getScaleController().setMaxScale(10.0f);
            deadmeme.getScaleController().setMinScale(0.01f);
            deadmeme.setLocalScale(new Vector3(1f, 1f, 1f));

            deadmeme.setParent(anchorNode);
            deadmeme.setRenderable(deadmemeRenderable);
            deadmeme.select();
        }
    }

    private void setClickListener() {
        for (int i = 0; i < arrayView.length; i++) {
            arrayView[i].setOnClickListener(this);
        }
    }

    private void setArrayView() {
        arrayView = new View[]{
                //add thumbnails here
                bear,  elephant, horse, koala, lion, reindeer, volcano, eiffel, merlion, deadmeme,
        };
    }

/*    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bear){
            selected = 1;
        }

        else if(view.getId() == R.id.elephant){
            selected =2;
        }

        else if(view.getId() == R.id.horse){
            selected =3;
        }
        else if(view.getId() == R.id.koalabear){
            selected =4;
        }
        else if(view.getId() == R.id.lion){
            selected =5;
        }
        else if(view.getId() == R.id.reindeer){
            selected =6;
        }
        else if(view.getId() == R.id.volcano){
            selected =7;
        }
        else if(view.getId() == R.id.eiffel){
            selected =8;
        }
        else if(view.getId() == R.id.merlion){
            selected =9;
        }
        else if(view.getId() == R.id.deadmeme){
            selected =10;
        }

    }
    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto() {
        final String filename = generateFilename();
        ArSceneView view = arFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("Open in Photos", v -> {
                    File photoFile = new File(filename);

                    Uri photoURI = FileProvider.getUriForFile(this,
                            this.getPackageName() + ".ar.codelab.name.provider",
                            photoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                });
                snackbar.show();
            } else {
                Toast toast = Toast.makeText(this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }
}