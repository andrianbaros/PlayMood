package com.example.playmood.view.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.playmood.R;
import com.example.playmood.network.respone.TrackItem;

import java.io.IOException;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.TrackViewHolder> {

    private final Context context;
    private final List<TrackItem> trackList;
    private MediaPlayer mediaPlayer;

    public PlaylistAdapter(Context context, List<TrackItem> trackList) {
        this.context = context;
        this.trackList = trackList;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        TrackItem track = trackList.get(position);
        holder.trackName.setText(track.name);
        holder.artistName.setText(track.artists[0].name);

        // Cek URL preview
        Log.d("SPOTIFY_PREVIEW", "Preview URL: " + track.preview_url);

        if (track.album.images.length > 0) {
            Glide.with(context)
                    .load(track.album.images[0].url)
                    .into(holder.albumImage);
        }

        // OnClick: play preview
        holder.itemView.setOnClickListener(v -> {
            String previewUrl = track.preview_url;
            Log.d("PreviewDebug", "Klik: " + track.name + " | URL: " + previewUrl);

            if (previewUrl == null || previewUrl.isEmpty()) {
                Toast.makeText(context, "Preview tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }

                mediaPlayer.setDataSource(previewUrl);
                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.d("PreviewDebug", "MediaPlayer siap, mulai mainkan");
                    mp.start();
                });
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e("PreviewDebug", "MediaPlayer error: " + what + ", extra: " + extra);
                    return true;
                });
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                Log.e("PreviewDebug", "Gagal setDataSource: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(context, "Gagal memutar preview", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void setTrackList(List<TrackItem> tracks) {
        trackList.clear();
        trackList.addAll(tracks);
        notifyDataSetChanged();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView trackName, artistName;
        ImageView albumImage;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.textTrackName);
            artistName = itemView.findViewById(R.id.textArtistName);
            albumImage = itemView.findViewById(R.id.imageAlbum);
        }
    }

    // Tambahkan method ini supaya bisa release MediaPlayer saat activity destroy
    public void releasePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
