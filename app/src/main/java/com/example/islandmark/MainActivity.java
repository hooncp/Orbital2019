package com.example.islandmark;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.islandmark.model.LandmarkDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity implements AccountFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener, MapViewFragment.OnFragmentInteractionListener,
        LandmarkFragment.OnFragmentInteractionListener, LandmarkDetailsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener{

    private FusedLocationProviderClient client;
    private ArrayList<LandmarkDetails> landmarkDetailsList = new ArrayList<>();
    private FirebaseAuth mAuth;
    public static String language = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        client = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();
        checkLocation();
        loadDetailsData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.navigation_showlandmarks:
                        Bundle args = new Bundle();
                        args.putParcelableArrayList("LANDMARKLIST", landmarkDetailsList);
                        fragment = new LandmarkFragment();
                        fragment.setArguments(args);
                        break;
                    case R.id.navigation_mapview:
                        fragment = new MapViewFragment();
                        break;
                    case R.id.navigation_account:
                        if(mAuth.getCurrentUser()== null) {
                            fragment = new AccountFragment();
                            break;
                        }
                        else{
                            fragment = new ProfileFragment();
                            break;
                        }
                }
                checkLocation();
                return displaySelectedScreen(fragment);
            }
        });
        displaySelectedScreen(new HomeFragment());
    }

    private void loadDetailsData() {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection(LandmarkDetails.landmarkDetailsKey).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // clean up the list to prevent double copies
                landmarkDetailsList = new ArrayList<>();
                landmarkDetailsList.removeAll(landmarkDetailsList);

                for (DocumentSnapshot document : documents) {

                    if (document.contains(LandmarkDetails.descriptionKey) && document.contains(LandmarkDetails.locationKey)
                            && document.contains(LandmarkDetails.nameKey)) {

                        String description = (String) document.get(LandmarkDetails.descriptionKey);
                        String name = (String) document.get(LandmarkDetails.nameKey);
                        GeoPoint location = (GeoPoint)document.get(LandmarkDetails.locationKey);
                        String documentID = (String) document.getId();
                        String timespent = (String)document.get(LandmarkDetails.timespentkey);
                        String descriptionlong = (String) document.get(LandmarkDetails.descriptionlongKey);
                        String type = (String) document.get(LandmarkDetails.typeKey);
                        LandmarkDetails details = new LandmarkDetails(description, name, location,documentID,descriptionlong,timespent,type);
                        landmarkDetailsList.add(details);
                    }
                }
            }
        });
    }

    public List<LandmarkDetails> getList(){
        return landmarkDetailsList;
    }

    //lat and longitude of current location stored in LandmarkDetails model.
    void checkLocation(){
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
        }
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LandmarkDetails.currentlat = latitude;
                    LandmarkDetails.currentlong = longitude;
                }
            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    public boolean displaySelectedScreen(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                changeLanguage();
                break;

            case R.id.logout:
                logout();
                break;

            case R.id.edit_profile:
                if (mAuth.getCurrentUser()!=null) {
                    Intent mainIntent = new Intent(MainActivity.this, SetupActivity.class);
                    startActivity(mainIntent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Please login before setting up profile",Toast.LENGTH_LONG).show();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragment_container, new AccountFragment())
                            .commit();

                }
                break;
        }

        //noinspection SimplifiableIfStatement
/*        if (id == R.id.action_settings) {
            changeLanguage();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    private void logout(){
        if(mAuth.getCurrentUser()==null){
            return;
        }
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Logout successful!",Toast.LENGTH_LONG).show();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

    }

    private void changeLanguage() {
        final String[] langList = {"English", "中文"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Change Language... ");
        AlertDialog.Builder builder = mBuilder.setSingleChoiceItems(langList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    language = "en";
                    setLocale("en");
                } else if (which == 1) {
                    language = "zh";
                    setLocale("zh");
                }
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
}
