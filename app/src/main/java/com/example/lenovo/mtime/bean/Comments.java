package com.example.lenovo.mtime.bean;

import android.graphics.Bitmap;

public class Comments {

    private String title;

    private String author;

    private String movieTitle;

    private Bitmap movieImage;

    private Bitmap authorImage;

    public Bitmap getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(Bitmap authorImage) {
        this.authorImage = authorImage;
    }

    private String summary;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Bitmap getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(Bitmap movieImage) {
        this.movieImage = movieImage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
