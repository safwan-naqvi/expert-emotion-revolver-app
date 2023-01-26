package com.example.socion.Dashboard.Songs;

public class SongsModel {
    String name, singer, genre, album, thumbnail,link;
    Double rating, Song_id;

    public SongsModel(String name, String singer, String genre, String album, String thumbnail, String link, Double rating, Double song_id) {
        this.name = name;
        this.singer = singer;
        this.genre = genre;
        this.album = album;
        this.thumbnail = thumbnail;
        this.link = link;
        this.rating = rating;
        Song_id = song_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Double getSong_id() {
        return Song_id;
    }

    public void setSong_id(Double song_id) {
        Song_id = song_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
