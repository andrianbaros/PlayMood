package com.example.playmood.network.respone;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackResponse {
    @SerializedName("tracks")
    public Tracks tracks;

    public static class Tracks {
        @SerializedName("items")
        public List<TrackItem> items;
    }
}