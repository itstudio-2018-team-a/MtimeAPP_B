package com.example.lenovo.mtime.bean;

import org.json.JSONArray;

public class Movie_details {

    private String title;

    private String image;

    private String mark;

    private String relase_date;

    private JSONArray replys;

    private String displayTime;

    private String comment_members;

    private String isMark;

    private String time;

    private String id;

    private String replyNum;

    private String marked_members;

    public String getMarked_members() {
        return marked_members;
    }

    public void setMarked_members(String marked_members) {
        this.marked_members = marked_members;
    }

    public String getIsMark() {
        return isMark;
    }

    public void setIsMark(String isMark) {
        this.isMark = isMark;
    }

    public String getRelase_date() {
        return relase_date;
    }

    public void setRelase_date(String relase_date) {
        this.relase_date = relase_date;
    }

    public String getComment_members() {
        return comment_members;
    }

    public void setComment_members(String comment_members) {
        this.comment_members = comment_members;
    }

    public JSONArray getReplys() {
        return replys;
    }

    public void setReplys(JSONArray replys) {
        this.replys = replys;
    }

    public String getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(String replyNum) {
        this.replyNum = replyNum;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMovieName(String movieName) {
        title = movieName;
    }

    public void setMovieId(String movieId) {
        id = movieId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public String getMovieName() {
        return title;
    }

    public String getMovieId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}


