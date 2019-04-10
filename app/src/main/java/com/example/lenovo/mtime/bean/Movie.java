package com.example.lenovo.mtime.bean;

public class Movie {

    private String title;

    private String image;

    private String info;

    private String release_date;

    private int film_id;

    private double mark;

    private int marked_members;

    private int commented_members;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getFilm_id() {
        return film_id;
    }

    public void setFilm_id(int film_id) {
        this.film_id = film_id;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getMarked_members() {
        return marked_members;
    }

    public void setMarked_members(int marked_members) {
        this.marked_members = marked_members;
    }

    public int getCommented_members() {
        return commented_members;
    }

    public void setCommented_members(int commented_members) {
        this.commented_members = commented_members;
    }
}
