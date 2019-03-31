package com.example.lenovo.mtime.bean;

import android.graphics.Bitmap;

public class NewsCom {
    private String content;
    private String author_id;
    private String author_name;
    private String author_head;
    private String time;
    private String comment_id;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_head() {
        return author_head;
    }

    public void setAuthor_head(String author_head) {
        this.author_head = author_head;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
