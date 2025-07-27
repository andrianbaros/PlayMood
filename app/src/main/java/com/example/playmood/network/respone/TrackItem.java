package com.example.playmood.network.respone;

import com.google.gson.annotations.SerializedName;

public class TrackItem {


    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("artists")
    public Artist[] artists;

    @SerializedName("album")
    public Album album;

    @SerializedName("preview_url")
    public String preview_url;

    public static class Artist {
        @SerializedName("name")
        public String name;
    }

    public static class Album {
        @SerializedName("images")
        public Image[] images;
    }

    public static class Image {
        @SerializedName("url")
        public String url;
    }
}
