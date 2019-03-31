package com.example.lenovo.mtime.bean;

import android.graphics.Bitmap;

public class News {

    private String title;

    private Bitmap picture;

    private String author;

    private String pub_time;

    private String news_id;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getNewsImage() {
        return picture;
    }

    public void setNewsImage(Bitmap newsImage) {
        this.picture = newsImage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return pub_time;
    }

    public void setTime(String time) {
        this.pub_time = time;
    }

    public String getNewsId() {
        return news_id;
    }

    public void setNewsId(String newsId) {
        this.news_id = newsId;
    }
}
