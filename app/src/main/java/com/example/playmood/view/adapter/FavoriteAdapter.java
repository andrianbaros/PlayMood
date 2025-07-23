package com.example.playmood.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.playmood.R;
import com.example.playmood.model.FavoriteModel;
import com.example.playmood.MusicPlayerActivity;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<FavoriteModel> favoriteList;
    private Context context;

    public FavoriteAdapter(Context context, List<FavoriteModel> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        FavoriteModel model = favoriteList.get(position);

        holder.title.setText(model.getTitle());
        holder.artist.setText(model.getArtist());
        Glide.with(context).load(model.getCover_url()).into(holder.cover);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("title", model.getTitle());
            intent.putExtra("artist", model.getArtist());
            intent.putExtra("cover_url", model.getCover_url());
            intent.putExtra("song_url", model.getSong_url());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, artist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imageCover);
            title = itemView.findViewById(R.id.textTitle);
            artist = itemView.findViewById(R.id.textArtist);
        }
    }
}