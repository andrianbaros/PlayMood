package com.example.playmood.model;

import android.content.Context;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyModel {

    private SpotifyAppRemote spotifyAppRemote;

    public interface SpotifyConnectionCallback {
        void onConnected(SpotifyAppRemote remote);
        void onFailure(Throwable error);
    }

    public void connect(Context context, String clientId, String redirectUri, SpotifyConnectionCallback callback) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(clientId)
                .setRedirectUri(redirectUri)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote remote) {
                spotifyAppRemote = remote;
                callback.onConnected(remote);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    public void disconnect() {
        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote);
        }
    }
}