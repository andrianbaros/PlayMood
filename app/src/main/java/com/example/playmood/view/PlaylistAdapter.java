package com.example.playmood.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.playmood.R;
import com.example.playmood.model.TrackItem;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.TrackViewHolder> {

    private final Context context;
    private final List<TrackItem> trackList;

    public PlaylistAdapter(Context context, List<TrackItem> trackList) {
        this.context = context;
        this.trackList = trackList;
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

        // Ambil gambar album pertama
        if (track.album.images.length > 0) {
            Glide.with(context)
                    .load(track.album.images[0].url)
                    .into(holder.albumImage);
        }
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    // Tambahkan method ini untuk update data list
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
}