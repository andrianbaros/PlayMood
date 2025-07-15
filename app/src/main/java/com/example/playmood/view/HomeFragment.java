package com.example.playmood.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;
    private LinearLayout songListContainer;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi view dari layout
        songListContainer = view.findViewById(R.id.songListContainer);

        // Inisialisasi presenter dan load data Supabase
        presenter = new HomePresenter(this);
        presenter.onHomeLoaded();

        return view;
    }

    @Override
    public void showAlbums(List<AlbumModel> albums) {
        Log.d("AlbumCheck", "Jumlah album: " + (albums != null ? albums.size() : 0));
        if (albums != null) {
            for (AlbumModel album : albums) {
                Log.d("AlbumCheck", "Judul: " + album.getTitle() + " | Artist: " + album.getArtist());
            }
        }

        if (songListContainer == null || getContext() == null) return;

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

            // Tombol play: buka MusicPlayerActivity dengan data album
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

    @Override
    public void showWelcomeMessage(String message) {
        // Contoh jika ingin menampilkan Toast ke pengguna
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
