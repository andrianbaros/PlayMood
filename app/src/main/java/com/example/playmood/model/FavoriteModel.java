package com.example.playmood.model;

public class FavoriteModel {
    private String id;
    private String user_id;
    private String title;
    private String artist;
    private String song_url;
    private String cover_url;
    private String created_at;

    public FavoriteModel() {} // required by Retrofit / Gson

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCover_url() {
        return cover_url;
    }

    public String getSong_url() {
        return song_url;
    }

    public String getCreated_at() {
        return created_at;
    }
}
