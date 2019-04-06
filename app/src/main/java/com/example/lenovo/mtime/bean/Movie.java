package com.example.lenovo.mtime.bean;

public class Movie {

    private String title;

    private String image;

    private String info;

    private String release_date;

    private String film_id;

    private String mark;

    private String marked_members;

    private String commented_members;


    public String getImage() {
        return image;
    }

    public String getMovieId() {
        return film_id;
    }

    public String getMovieInfo() {
        return info;
    }

    public String getMovieName() {
        return title;
    }

    public String getTime() {
        return release_date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMovieId(String movieId) {
        film_id = movieId;
    }

    public void setMovieInfo(String movieInfo) {
        info = movieInfo;
    }

    public void setMovieName(String movieName) {
        title = movieName;
    }

    public void setTime(String time) {
        release_date = time;
    }


    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        mark = mark;
    }

    public String getMarked_members() {
        return marked_members;
    }

    public void setMarked_members(String marked_members) {
        this.marked_members = marked_members;
    }

    public String getCommented_members() {
        return commented_members;
    }

    public void setCommented_members(String commented_members) {
        this.commented_members = commented_members;
    }
}
