package com.example.islandmark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.*;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Ar_activity extends AppCompatActivity implements View.OnClickListener {

    private ArFragment arFragment;
    private ModelRenderable bearRenderable,
            catRenderable,
            cowRenderable,
            dogRenderable,
            elephantRenderable,
            ferretRenderable,
            hippopotamusRenderable,
            horseRenderable,
            koalaRenderable,
            lionRenderable,
            reindeerRenderable,
            wolverineRenderable;

    ImageView bear, cat, cow, dog, elephant, ferret, hippopotamus, horse, koala, lion, reindeer, wolverine;

    //for the future when we import more models
    View arrayView[];

    int selected = 1;

    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ar);
        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        bear = findViewById(R.id.bear);
        cat = findViewById(R.id.cat);
        cow = findViewById(R.id.cow);
        dog = findViewById(R.id.dog);
        elephant = findViewById(R.id.elephant);
        ferret = findViewById(R.id.ferret);
        hippopotamus = findViewById(R.id.hippopotamus);
        horse = findViewById(R.id.horse);
        koala = findViewById(R.id.koalabear);
        lion = findViewById(R.id.lion);
        reindeer = findViewById(R.id.reindeer);
        wolverine = findViewById(R.id.wolverine);

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
        FloatingActionButton btnPhoto = findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(view -> takePhoto());
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
                .setSource(this,R.raw.cat)
                .build().thenAccept(renderable -> catRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load cat model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this,R.raw.cow)
                .build().thenAccept(renderable -> cowRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load cow model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );
        ModelRenderable.builder()
                .setSource(this,R.raw.dog)
                .build().thenAccept(renderable -> dogRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load dog model",Toast.LENGTH_SHORT);
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
                .setSource(this,R.raw.ferret)
                .build().thenAccept(renderable -> ferretRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load ferret model",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            return null;
                        }
                );
        ModelRenderable.builder()
                .setSource(this,R.raw.hippopotamus)
                .build().thenAccept(renderable -> hippopotamusRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load hippopotamus model",Toast.LENGTH_SHORT);
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
                .setSource(this,R.raw.wolverine)
                .build().thenAccept(renderable -> wolverineRenderable = renderable)
                .exceptionally(
                        throwable ->{
                            Toast toast=Toast.makeText(getApplicationContext(),"unable to load wolverine model",Toast.LENGTH_SHORT);
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
            TransformableNode cat = new TransformableNode(arFragment.getTransformationSystem());
            cat.getScaleController().setMaxScale(10.0f);
            cat.getScaleController().setMinScale(0.01f);
            cat.setLocalScale(new Vector3(10f,10f,10f));

            cat.setParent(anchorNode);
            cat.setRenderable(catRenderable);
            cat.select();
        }

        if(selected == 3){
            TransformableNode cow = new TransformableNode(arFragment.getTransformationSystem());
            cow.getScaleController().setMaxScale(10.0f);
            cow.getScaleController().setMinScale(0.01f);
            cow.setLocalScale(new Vector3(10f,10f,10f));


            cow.setParent(anchorNode);
            cow.setRenderable(cowRenderable);
            cow.select();
        }

        if(selected == 4){
            TransformableNode dog = new TransformableNode(arFragment.getTransformationSystem());
            dog.getScaleController().setMaxScale(10.0f);
            dog.getScaleController().setMinScale(0.01f);
            dog.setLocalScale(new Vector3(10f,10f,10f));

            dog.setParent(anchorNode);
            dog.setRenderable(dogRenderable);
            dog.select();
        }

        if(selected == 5){
            TransformableNode elephant = new TransformableNode(arFragment.getTransformationSystem());
            elephant.getScaleController().setMaxScale(10.0f);
            elephant.getScaleController().setMinScale(0.01f);
            elephant.setLocalScale(new Vector3(10f,10f,10f));

            elephant.setParent(anchorNode);
            elephant.setRenderable(elephantRenderable);
            elephant.select();
        }

        if(selected == 6){
            TransformableNode ferret = new TransformableNode(arFragment.getTransformationSystem());
            ferret.getScaleController().setMaxScale(10.0f);
            ferret.getScaleController().setMinScale(0.01f);
            ferret.setLocalScale(new Vector3(10f,10f,10f));

            ferret.setParent(anchorNode);
            ferret.setRenderable(ferretRenderable);
            ferret.select();
        }

        if(selected == 7){
            TransformableNode hippopotamus = new TransformableNode(arFragment.getTransformationSystem());
            hippopotamus.getScaleController().setMaxScale(10.0f);
            hippopotamus.getScaleController().setMinScale(0.01f);
            hippopotamus.setLocalScale(new Vector3(10f,10f,10f));

            hippopotamus.setParent(anchorNode);
            hippopotamus.setRenderable(hippopotamusRenderable);
            hippopotamus.select();
        }

        if(selected == 8){
            TransformableNode horse = new TransformableNode(arFragment.getTransformationSystem());
            horse.getScaleController().setMaxScale(10.0f);
            horse.getScaleController().setMinScale(0.01f);
            horse.setLocalScale(new Vector3(10f,10f,10f));

            horse.setParent(anchorNode);
            horse.setRenderable(horseRenderable);
            horse.select();
        }

        if(selected == 9){
            TransformableNode koala_bear = new TransformableNode(arFragment.getTransformationSystem());
            koala_bear.getScaleController().setMaxScale(10.0f);
            koala_bear.getScaleController().setMinScale(0.01f);
            koala_bear.setLocalScale(new Vector3(10f,10f,10f));

            koala_bear.setParent(anchorNode);
            koala_bear.setRenderable(koalaRenderable);
            koala_bear.select();
        }

        if(selected == 10){
            TransformableNode lion = new TransformableNode(arFragment.getTransformationSystem());
            lion.getScaleController().setMaxScale(10.0f);
            lion.getScaleController().setMinScale(0.01f);
            lion.setLocalScale(new Vector3(10f,10f,10f));

            lion.setParent(anchorNode);
            lion.setRenderable(lionRenderable);
            lion.select();
        }

        if(selected == 11){
            TransformableNode reindeer = new TransformableNode(arFragment.getTransformationSystem());
            reindeer.getScaleController().setMaxScale(10.0f);
            reindeer.getScaleController().setMinScale(0.01f);
            reindeer.setLocalScale(new Vector3(10f,10f,10f));

            reindeer.setParent(anchorNode);
            reindeer.setRenderable(reindeerRenderable);
            reindeer.select();
        }

        if(selected == 12){
            TransformableNode wolverine = new TransformableNode(arFragment.getTransformationSystem());
            wolverine.getScaleController().setMaxScale(10.0f);
            wolverine.getScaleController().setMinScale(0.01f);
            wolverine.setLocalScale(new Vector3(10f,10f,10f));

            wolverine.setParent(anchorNode);
            wolverine.setRenderable(wolverineRenderable);
            wolverine.select();
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
                bear, cat, cow, dog, elephant, ferret, hippopotamus, horse, koala, lion, reindeer, wolverine
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
        else if(view.getId() == R.id.cat){
            selected =2;
        }
        else if(view.getId() == R.id.cow){
            selected =3;
        }
        else if(view.getId() == R.id.dog){
            selected =4;
        }
        else if(view.getId() == R.id.elephant){
            selected =5;
        }
        else if(view.getId() == R.id.ferret){
            selected =6;
        }
        else if(view.getId() == R.id.hippopotamus){
            selected =7;
        }
        else if(view.getId() == R.id.horse){
            selected =8;
        }
        else if(view.getId() == R.id.koalabear){
            selected =9;
        }
        else if(view.getId() == R.id.lion){
            selected =10;
        }
        else if(view.getId() == R.id.reindeer){
            selected =11;
        }
        else if(view.getId() == R.id.wolverine){
            selected =12;
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