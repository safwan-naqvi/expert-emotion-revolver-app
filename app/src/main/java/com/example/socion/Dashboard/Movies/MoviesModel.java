package com.example.socion.Dashboard.Movies;

import java.io.Serializable;

public class MoviesModel implements Serializable {
    String title, director, starring, category, duration, censor, videoID, image;
    Double released;
    Double rating;
    Double item_id;

    public MoviesModel(String title, String director, String starring, String category, String duration, String censor, String videoID, String image, Double released, Double rating, Double item_id) {
        this.title = title;
        this.director = director;
        this.starring = starring;
        this.category = category;
        this.duration = duration;
        this.censor = censor;
        this.videoID = videoID;
        this.image = image;
        this.released = released;
        this.rating = rating;
        this.item_id = item_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStarring() {
        return starring;
    }

    public void setStarring(String starring) {
        this.starring = starring;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCensor() {
        return censor;
    }

    public void setCensor(String censor) {
        this.censor = censor;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getReleased() {
        return released;
    }

    public void setReleased(Double released) {
        this.released = released;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getItem_id() {
        return item_id;
    }

    public void setItem_id(Double item_id) {
        this.item_id = item_id;
    }
}
