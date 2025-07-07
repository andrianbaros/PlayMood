package com.example.playmood.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playmood.spotify.presenter.SpotifyPresenter;
import com.example.playmood.spotify.view.SpotifyView;

public class MainActivity extends AppCompatActivity implements SpotifyView {

    private SpotifyPresenter spotifyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Sesuaikan layout-nya

        spotifyPresenter = new SpotifyPresenter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spotifyPresenter.connectToSpotify(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        spotifyPresenter.disconnectSpotify();
    }

    @Override
    public void onSpotifyConnected() {
        Toast.makeText(this, "Berhasil terhubung ke Spotify!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpotifyConnectionFailed(String error) {
        Toast.makeText(this, "Gagal terhubung: " + error, Toast.LENGTH_LONG).show();
        Log.e("MainActivity", "Spotify connection error: " + error);
    }
}