package com.example.islandmark;

public class Review {
    public String username, review;

    public Review() {

    }

    //add more variables in the future e.g. pictures, time etc

    public Review(String username, String review){
        this.username = username;
        this.review = review;
    }

    public String getUsername() {
        return username;
    }

    public String getUser_review() {
        return review;
    }

}
