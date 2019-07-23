package com.example.islandmark;

public class Review {
    public String username, review, profileimage;

    public Review() {

    }

    //add more variables in the future e.g. pictures, time etc

    public Review(String username, String review, String profileimage){
        this.username = username;
        this.review = review;
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public String getUser_review() {
        return review;
    }

    public String getProfileimage(){return profileimage;}

}
