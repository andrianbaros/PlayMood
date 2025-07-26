package com.example.playmood.view.music.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playmood.R;
import com.example.playmood.view.adapter.PlaylistAdapter;
import com.example.playmood.network.respone.TrackItem;
import com.example.playmood.presenter.PlaylistPresenter;
import com.example.playmood.view.music.view.PlaylistView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements PlaylistView {

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private PlaylistPresenter presenter;

    private List<TrackItem> trackList = new ArrayList<>();

    // Ganti dengan mood yang terdeteksi, bisa juga diambil dari Intent
    private String detectedMood = "happy";  // default

    // Ganti ini dengan Client ID dan Secret milikmu
    private final String CLIENT_ID = "553f775bb7d14d7c99dbdae9285c1ac8";
    private final String CLIENT_SECRET = "01544f49eb824c449e1d2315703eb0e7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        recyclerView = findViewById(R.id.recyclerPlaylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlaylistAdapter(this, trackList);
        recyclerView.setAdapter(adapter);

        presenter = new PlaylistPresenter(this);

        // Dapatkan mood dari intent jika dikirim
        if (getIntent() != null && getIntent().hasExtra("mood")) {
            detectedMood = getIntent().getStringExtra("mood");
        }

        presenter.fetchAccessToken(CLIENT_ID, CLIENT_SECRET);
    }

    @Override
    public void onTokenReceived(String accessToken) {
        presenter.fetchTracks(accessToken, detectedMood);
    }

    @Override
    public void onTracksReceived(List<TrackItem> tracks) {
        adapter.setTrackList(tracks);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

}}
