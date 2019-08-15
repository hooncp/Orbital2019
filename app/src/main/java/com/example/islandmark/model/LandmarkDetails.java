package com.example.islandmark.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class LandmarkDetails implements Parcelable {
    public static final String landmarkDetailsKey = "landmarks"; //collection
    public static final String descriptionKey = "description"; //field
    public static final String descriptionlongKey = "descriptionlong"; //field
    public static final String locationKey = "location"; //field
    public static final String nameKey = "name"; //field
    public static final String timespentkey = "timespent";
    public static final String typeKey = "type";

    public static double currentlat;
    public static double currentlong;

    public String description;
    public String descriptionlong;
    public String name;
    public GeoPoint location;
    public String timespent;
    private String documentID;
    public int distance;
    public String type;
    public String namezh;

//    public LandmarkDetails (String description , String name, GeoPoint location,
//                            String documentID, String descriptionlong, String timespent,
//                            String type){
//        this.description = description;
//        this.name = name;
//        this.location = location;
//        this.documentID = documentID;
//        this.descriptionlong = descriptionlong;
//        this.timespent = timespent;
//        this.type = type;
//
//    }
    public LandmarkDetails (String description , String name, GeoPoint location,
                            String documentID, String descriptionlong, String timespent,
                            String type, String namezh){
        this.description = description;
        this.name = name;
        this.location = location;
        this.documentID = documentID;
        this.descriptionlong = descriptionlong;
        this.timespent = timespent;
        this.type = type;
        this.namezh = namezh;

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
        int finalvalue = ((int)Math.round(d/10)) * 10;
        return finalvalue;
    }
    public String getlinkURL(){
        String temp = "landmarks/";
        temp = temp + documentID +"/Image1.jpg";
        return temp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(documentID);
        dest.writeInt(distance);
    }
    protected LandmarkDetails(Parcel in) {
        description = in.readString();
        name = in.readString();
        documentID = in.readString();
        distance = in.readInt();
    }

    public static final Creator<LandmarkDetails> CREATOR = new Creator<LandmarkDetails>() {
        @Override
        public LandmarkDetails createFromParcel(Parcel in) {
            return new LandmarkDetails(in);
        }

        @Override
        public LandmarkDetails[] newArray(int size) {
            return new LandmarkDetails[size];
        }
    };

}
