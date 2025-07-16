package com.example.playmood.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;
    private LinearLayout songListContainer;
    private List<AlbumModel> fullAlbumList = new ArrayList<>();

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        songListContainer = view.findViewById(R.id.songListContainer);
        SearchView searchView = view.findViewById(R.id.searchView);

        // SearchView selalu terlihat (dari XML) -> tambahkan pencarian
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAlbums(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAlbums(newText);
                return true;
            }
        });

        // Inisialisasi presenter dan ambil data
        presenter = new HomePresenter(this);
        presenter.onHomeLoaded();

        return view;
    }

    @Override
    public void showAlbums(List<AlbumModel> albums) {
        if (albums == null || getContext() == null) return;

        Log.d("AlbumCheck", "Jumlah album: " + albums.size());
        fullAlbumList = albums; // Simpan untuk pencarian/filter
        displayAlbums(albums);
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

            playButton.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
                intent.putExtra("TITLE", album.getTitle());
                intent.putExtra("ARTIST", album.getArtist());
                intent.putExtra("COVER", album.getCoverUrl());
                intent.putExtra("SONGURL", album.getSongUrl());
                startActivity(intent);
            });

            songListContainer.addView(itemView);
        }
    }

    private void filterAlbums(String query) {
        List<AlbumModel> filteredList = new ArrayList<>();

        for (AlbumModel album : fullAlbumList) {
            if (album.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    album.getArtist().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(album);
            }
        }

        displayAlbums(filteredList);
    }

    @Override
    public void showWelcomeMessage(String message) {
        // Optional: Tampilkan pesan selamat datang
    }
}