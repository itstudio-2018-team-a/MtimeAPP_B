package com.example.lenovo.mtime.bean;

public class Movie {

    private String MovieName;

    private String image;   //image是传送地址过来吗？

    private String MovieInfo;

    private String Time;

    private String MovieId;

    private String Mark;


    public String getImage() {
        return image;
    }

    public String getMovieId() {
        return MovieId;
    }

    public String getMovieInfo() {
        return MovieInfo;
    }

    public String getMovieName() {
        return MovieName;
    }

    public String getTime() {
        return Time;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMovieId(String movieId) {
        MovieId = movieId;
    }

    public void setMovieInfo(String movieInfo) {
        MovieInfo = movieInfo;
    }

    public void setMovieName(String movieName) {
        MovieName = movieName;
    }

    public void setTime(String time) {
        Time = time;
    }


    public String getMark() {
        return Mark;
    }

    public void setMark(String mark) {
        Mark = mark;
    }
}
