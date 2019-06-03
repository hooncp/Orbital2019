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
    private String documentID;

    public LandmarkDetails (String description , String name, GeoPoint location, String documentID){
        this.description = description;
        this.name = name;
        this.location = location;
        this.documentID = documentID;
    }

    public String getDocumentID() {
        return documentID;
    }
//    @Override
//    public String toString() {
//        String final1 = name + " " + description;
//        return final1;
//    }
}
