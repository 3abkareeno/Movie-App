package com.example.android.data;

import java.io.Serializable;

public class Movie implements Serializable{
    private String posterpath;
    private String title;
    private boolean adult;
    private String description;
    private String releaseDate;
    private String id;
    private String userVote;

    public Movie(String posterpath, String title, boolean adult, String description, String releaseDate, String id, String userVote) {
        this.posterpath = posterpath;
        this.title = title;
        this.adult = adult;
        this.description = description;
        this.releaseDate = releaseDate;
        this.id = id;
        this.userVote = userVote;
    }

    public String getPosterpath() {
        return posterpath;
    }

    public void setPosterpath(String posterpath) {
        this.posterpath = posterpath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserVote() {
        return userVote;
    }

    public void setUserVote(String userVote) {
        this.userVote = userVote;
    }
}
