package com.example.lenovo.mtime.bean;

public class Movie_details {

    private String MovieName;

    private String image;   //image是传送地址过来吗？

    private String MovieInfo;

    private String Time;

    private String MovieId;

    private String status;

    public void setTime(String time) {
        Time = time;
    }

    public void setMovieName(String movieName) {
        MovieName = movieName;
    }

    public void setMovieInfo(String movieInfo) {
        MovieInfo = movieInfo;
    }

    public void setMovieId(String movieId) {
        MovieId = movieId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return Time;
    }

    public String getMovieName() {
        return MovieName;
    }

    public String getMovieInfo() {
        return MovieInfo;
    }

    public String getMovieId() {
        return MovieId;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}


