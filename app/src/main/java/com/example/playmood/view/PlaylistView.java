package com.example.playmood.view;

import com.example.playmood.model.TrackItem;

import java.util.List;

public interface PlaylistView {
    void onTokenReceived(String accessToken);
    void onTracksReceived(List<TrackItem> tracks);
    void onError(String message);
}