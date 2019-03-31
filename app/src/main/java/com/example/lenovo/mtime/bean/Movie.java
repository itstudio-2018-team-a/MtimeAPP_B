package com.example.lenovo.mtime.bean;

public class Movie {

    private String title;

    private String image;   //image是传送地址过来吗？

    private String info;

    private String release_date;

    private String film_id;

    private String mark;


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
}
