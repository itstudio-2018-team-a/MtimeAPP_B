package com.example.lenovo.mtime.bean;

import android.graphics.Bitmap;

public class NewsCom {
    private int id;
    private String author;
    private String autherHeadPhoto;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAutherHeadPhoto() {
        return autherHeadPhoto;
    }

    public void setAutherHeadPhoto(String autherHeadPhoto) {
        this.autherHeadPhoto = autherHeadPhoto;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
