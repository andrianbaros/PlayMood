package com.example.playmood.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.playmood.MusicPlayerActivity;
import com.example.playmood.R;
import com.example.playmood.model.AlbumModel;
import com.example.playmood.presenter.HomePresenter;
import com.example.playmood.presenter.contract.HomeContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;
    private LinearLayout songListContainer;
    private LinearLayout playlistListContainer;
    private List<AlbumModel> fullAlbumList = new ArrayList<>();
    private AutoCompleteTextView searchView;

    private List<AlbumModel> publicPlaylists = new ArrayList<>();

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        songListContainer = view.findViewById(R.id.songListContainer);
        playlistListContainer = view.findViewById(R.id.playlistListContainer);
        searchView = view.findViewById(R.id.searchAutoComplete);

        // Tambahkan listener saat user mengetik
        searchView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPlaylists(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Tangani saat item suggestion diklik
        searchView.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            searchView.setText(selected);

            // Temukan lagu yang cocok dari list
            for (AlbumModel album : fullAlbumList) {
                if (album.getTitle().equalsIgnoreCase(selected) || album.getArtist().equalsIgnoreCase(selected)) {
                    openPlayer(album);
                    break; // stop setelah ketemu
                }
            }
        });

        // Presenter ambil data album
        presenter = new HomePresenter(this);
        presenter.onHomeLoaded();

        return view;
    }

    @Override
    public void showAlbums(List<AlbumModel> albums) {
        if (albums == null || getContext() == null) return;

        Log.d("AlbumCheck", "Jumlah album: " + albums.size());
        fullAlbumList = albums;

        // Tampilkan lagu populer
        showPopularSongs();

        // Simpan dan tampilkan playlist publik (10 pertama)
        publicPlaylists = fullAlbumList.size() > 10
                ? fullAlbumList.subList(0, 10)
                : new ArrayList<>(fullAlbumList);

        displayPlaylists(publicPlaylists); // ✅ tampilkan playlist awal tanpa filter

        // Optional: tampilkan suggestion lain kalau kamu ingin ada autocomplete, dsb
        showSuggestions();
    }

    private void showSuggestions() {
        List<String> suggestions = new ArrayList<>();

        for (AlbumModel album : fullAlbumList) {
            suggestions.add(album.getTitle());
            suggestions.add(album.getArtist());
        }

        // Hapus duplikat
        Set<String> uniqueSet = new LinkedHashSet<>(suggestions);
        List<String> uniqueSuggestions = new ArrayList<>(uniqueSet);

        // Pasang adapter untuk suggestion dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                uniqueSuggestions
        );

        searchView.setAdapter(adapter);
        searchView.setThreshold(1); // Tampilkan suggestion setelah 1 karakter
    }

    private void showPopularSongs() {
        List<AlbumModel> randomAlbums = new ArrayList<>(fullAlbumList);
        Collections.shuffle(randomAlbums);
        if (randomAlbums.size() > 5) {
            randomAlbums = randomAlbums.subList(0, 5);
        }
        displayAlbums(randomAlbums);
    }

    private void displayAlbums(List<AlbumModel> albums) {
        songListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (AlbumModel album : albums) {
            View itemView = inflater.inflate(R.layout.item_album, songListContainer, false);

            TextView textTitle = itemView.findViewById(R.id.textTitle);
            TextView textArtist = itemView.findViewById(R.id.textArtist);
            ImageView imageCover = itemView.findViewById(R.id.imageCover);
            ImageView playButton = itemView.findViewById(R.id.playButton);

            textTitle.setText(album.getTitle());
            textArtist.setText(album.getArtist());

            Glide.with(getContext())
                    .load(album.getCoverUrl())
                    .placeholder(R.drawable.rounded_image_background)
                    .centerCrop()
                    .into(imageCover);

            playButton.setOnClickListener(v -> openPlayer(album));
            songListContainer.addView(itemView);
        }
    }
    private void fetchDuration(AlbumModel album, TextView durationView) {
        // Jika sudah ada, tidak perlu fetch lagi
        if (album.getTime() != null && !album.getTime().isEmpty()) {
            durationView.setText(album.getTime());
            return;
        }

        durationView.setText("..."); // tampilkan loading sementara

        android.media.MediaPlayer mediaPlayer = new android.media.MediaPlayer();
        try {
            mediaPlayer.setDataSource(album.getPreviewUrl() != null ? album.getPreviewUrl() : album.getSongUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                int durationMs = mp.getDuration(); // durasi dalam milidetik
                int minutes = (durationMs / 1000) / 60;
                int seconds = (durationMs / 1000) % 60;
                String timeFormatted = String.format("%d:%02d", minutes, seconds);
                durationView.setText(timeFormatted);
                album.setTime(timeFormatted); // simpan agar tidak load ulang
                mp.release();
            });
        } catch (Exception e) {
            durationView.setText("0:00");
            e.printStackTrace();
        }
    }


    private void displayPlaylists(List<AlbumModel> playlists) {
        playlistListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        List<AlbumModel> limitedPlaylists = playlists.size() > 10
                ? playlists.subList(0, 10)
                : playlists;

        for (AlbumModel playlist : limitedPlaylists) {
            View itemView = inflater.inflate(R.layout.item_playlist, playlistListContainer, false);

            ImageView imageView = itemView.findViewById(R.id.imageView);
            TextView title = itemView.findViewById(R.id.textViewTitle);
            TextView artist = itemView.findViewById(R.id.textViewSubtitle);
            TextView duration = itemView.findViewById(R.id.textViewDuration);
            ImageView playBtn = itemView.findViewById(R.id.playButton);

            title.setText(playlist.getTitle());
            artist.setText(playlist.getArtist());

            fetchDuration(playlist, duration);

            Glide.with(getContext())
                    .load(playlist.getCoverUrl())
                    .placeholder(R.drawable.rounded_image_background)
                    .into(imageView);

            itemView.setOnClickListener(v -> openPlayer(playlist));
            playlistListContainer.addView(itemView);
        }
    }


    private void filterPlaylists(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Kalau kosong, tampilkan playlist publik (default)
            displayPlaylists(publicPlaylists);
            return;
        }

        List<AlbumModel> filteredList = new ArrayList<>();
        for (AlbumModel album : fullAlbumList) {
            if (album.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    album.getArtist().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(album);
            }
        }

        displayPlaylists(filteredList);
    }

    private void openPlayer(AlbumModel album) {
        Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
        intent.putExtra("TITLE", album.getTitle());
        intent.putExtra("ARTIST", album.getArtist());
        intent.putExtra("COVER", album.getCoverUrl());
        intent.putExtra("SONGURL", album.getSongUrl());
        startActivity(intent);
    }

    @Override
    public void showWelcomeMessage(String message) {
        // Optional
    }
}
