package com.example.playmood.view.music.view;

import com.example.playmood.network.respone.TrackItem;

import java.util.List;

public interface PlaylistView {
    void onTokenReceived(String accessToken);
    void onTracksReceived(List<TrackItem> tracks);
    void onError(String message);
}