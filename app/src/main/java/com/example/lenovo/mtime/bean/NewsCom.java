package com.example.lenovo.mtime.bean;

import android.graphics.Bitmap;

public class NewsCom {

    private String author;

    private String NewsId;

    private Bitmap authorImage;

    private String time;

    private String context;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNewsId() {
        return NewsId;
    }

    public void setNewsId(String newsId) {
        NewsId = newsId;
    }

    public Bitmap getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(Bitmap authorImage) {
        this.authorImage = authorImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
