package com.example.socion.Dashboard;

public class YoutubeModel {
    String video_title,video_genre,video_starring,video_rating,video_director,video_id,video_thumbnail;

    public YoutubeModel(){}

    public YoutubeModel(String video_title, String video_genre, String video_starring, String video_rating, String video_director, String video_id, String video_thumbnail) {
        this.video_title = video_title;
        this.video_genre = video_genre;
        this.video_starring = video_starring;
        this.video_rating = video_rating;
        this.video_director = video_director;
        this.video_id = video_id;
        this.video_thumbnail = video_thumbnail;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_genre() {
        return video_genre;
    }

    public void setVideo_genre(String video_genre) {
        this.video_genre = video_genre;
    }

    public String getVideo_starring() {
        return video_starring;
    }

    public void setVideo_starring(String video_starring) {
        this.video_starring = video_starring;
    }

    public String getVideo_rating() {
        return video_rating;
    }

    public void setVideo_rating(String video_rating) {
        this.video_rating = video_rating;
    }

    public String getVideo_director() {
        return video_director;
    }

    public void setVideo_director(String video_director) {
        this.video_director = video_director;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }
}
