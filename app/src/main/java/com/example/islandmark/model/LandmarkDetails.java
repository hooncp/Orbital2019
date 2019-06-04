package com.example.islandmark.model;

import com.google.firebase.firestore.GeoPoint;

public class LandmarkDetails implements Comparable<LandmarkDetails>{
    public static final String landmarkDetailsKey = "landmarks"; //collection
    public static final String descriptionKey = "description"; //field
    public static final String locationKey = "location"; //field
    public static final String nameKey = "name"; //field

    public static double currentlat;
    public static double currentlong;

    public String description;
    public String name;
    public GeoPoint location;
    private String documentID;
    public int distance;

    public LandmarkDetails (String description , String name, GeoPoint location, String documentID){
        this.description = description;
        this.name = name;
        this.location = location;
        this.documentID = documentID;
        this.distance = getDistance();
    }

    public String getDocumentID() {
        return documentID;
    }

    public int getDistance() {
        double lat1 = currentlat;
        double long1 = currentlong;
        double lat2 = location.getLatitude();
        double long2 = location.getLongitude();

        double R = 6371000; // metres
        double φ1 = Math.toRadians(lat2);
        double φ2 = Math.toRadians(lat1);
        double Δφ = Math.toRadians(lat1-lat2);
        double Δλ = Math.toRadians(long1-long2);
        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ/2) * Math.sin(Δλ/2) ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        int ten = 10;
        int finalvalue = ((int)Math.round(d/10)) * ten;
        return finalvalue;
    }

    @Override
    public int compareTo(LandmarkDetails o) {
        int a = o.distance;
        int b = this.distance;
        return a - b;
    }

}
