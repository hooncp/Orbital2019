package com.example.islandmark.model;

import com.google.firebase.firestore.GeoPoint;

public class LandmarkDetails {
    public static final String landmarkDetailsKey = "landmarks"; //collection
    public static final String descriptionKey = "description"; //field
    public static final String locationKey = "location"; //field
    public static final String nameKey = "name"; //field

    public String description;
    public String name;
    public GeoPoint location;

    public LandmarkDetails (String description , String name, GeoPoint location){
        this.description = description;
        this.name = name;
        this.location = location;
    }
//    @Override
//    public String toString() {
//        String final1 = name + " " + description;
//        return final1;
//    }
}
