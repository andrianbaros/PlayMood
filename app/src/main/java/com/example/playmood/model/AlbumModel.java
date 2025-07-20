package com.example.playmood.model;

import java.util.List;

public class AlbumModel {
    private String title;
    private List<String> artist;
    private String cover_url;
    private String song_url;
    private String mood;
    private String previewUrl;

    public AlbumModel(String title, List<String> artist, String coverUrl, String songUrl,String previewUrl) {
        this.title = title;
        this.artist = artist;
        this.cover_url = coverUrl;
        this.song_url = songUrl;
        this.mood = mood;
        this.previewUrl = previewUrl;
    }

    public String getTitle() { return title; }
    public List<String> getArtistList() { return artist; }
    public String getArtist() {
        return String.join(", ", artist); // Gabungkan
    }
    public String getCoverUrl() { return cover_url; }
    public String getSongUrl() { return song_url; }

    public String getMood() { return mood; }
    public String getPreviewUrl() { return previewUrl; }
}