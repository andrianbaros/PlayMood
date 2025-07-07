package com.example.playmood.presenter;

import android.content.Context;
import android.util.Log;

import com.example.playmood.spotify.model.SpotifyModel;
import com.example.playmood.spotify.view.SpotifyView;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyPresenter {

    private final SpotifyView view;
    private final SpotifyModel model;

    private static final String CLIENT_ID = "ISI_CLIENT_ID_KAMU"; // Ganti sesuai kamu
    private static final String REDIRECT_URI = "yourapp://callback";

    public SpotifyPresenter(SpotifyView view) {
        this.view = view;
        this.model = new SpotifyModel();
    }

    public void connectToSpotify(Context context) {
        model.connect(context, CLIENT_ID, REDIRECT_URI, new SpotifyModel.SpotifyConnectionCallback() {
            @Override
            public void onConnected(SpotifyAppRemote remote) {
                view.onSpotifyConnected();

                // Putar lagu (URI bebas kamu ganti)
                remote.getPlayerApi().play("spotify:track:4cOdK2wGLETKBW3PvgPWqT");
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("SpotifyPresenter", "Connection failed", error);
                view.onSpotifyConnectionFailed(error.getMessage());
            }
        });
    }

    public void disconnectSpotify() {
        model.disconnect();
    }
}
