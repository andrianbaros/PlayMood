package com.example.playmood.model;

public class FavoriteRequest {
    private String user_id, title, artist, song_url, cover_url;

    public FavoriteRequest(String user_id, String title, String artist, String song_url, String cover_url) {
        this.user_id = user_id;
        this.title = title;
        this.artist = artist;
        this.song_url = song_url;
        this.cover_url = cover_url;
    }

    // Getter & Setter (jika dibutuhkan)
}