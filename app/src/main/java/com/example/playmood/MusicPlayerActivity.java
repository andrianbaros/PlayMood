package com.example.playmood;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.playmood.network.RetrofitClient;
import com.example.playmood.network.SupabaseFavoriteService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicPlayerActivity extends AppCompatActivity {

    private ImageView albumArt;
    private TextView songTitle, songArtist, startTimeText, endTimeText;
    private ImageButton btnPlayPause, btnFavorite;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBar;

    private String title, artist, coverUrl, songUrl, userId;
    private boolean isPlaying = false;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        // Inisialisasi View
        albumArt = findViewById(R.id.albumArt);
        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnFavorite = findViewById(R.id.btnFavorite);
        seekBar = findViewById(R.id.seekBar);
        startTimeText = findViewById(R.id.startTime);
        endTimeText = findViewById(R.id.endTime);

        // Ambil data dari intent
        Intent intent = getIntent();
        title = intent.getStringExtra("TITLE");
        artist = intent.getStringExtra("ARTIST");
        coverUrl = intent.getStringExtra("COVER");
        songUrl = intent.getStringExtra("SONGURL");

        // Ambil userId dari Firebase
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tampilkan info lagu
        songTitle.setText(title);
        songArtist.setText(artist);
        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.rounded_image_background)
                .centerCrop()
                .into(albumArt);

        // Tombol kembali
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        // Tombol yang belum diimplementasikan
        findViewById(R.id.btnNext).setOnClickListener(v -> {});
        findViewById(R.id.btnPrevious).setOnClickListener(v -> {});
        findViewById(R.id.btnShuffle).setOnClickListener(v -> {});
        findViewById(R.id.btnRepeat).setOnClickListener(v -> {});

        // Setup favorit
        btnFavorite.setSelected(false);
        btnFavorite.setOnClickListener(v -> {
            if (!isFavorite) {
                saveFavoriteToSupabase();
            } else {
                removeFavoriteFromSupabase();
            }
        });

        // Setup seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Tombol play/pause
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        // Cek favorit
        checkIfFavorite();
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    isPlaying = true;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);

                    seekBar.setMax(mp.getDuration());
                    endTimeText.setText(formatTime(mp.getDuration()));

                    updateSeekBar = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                int current = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(current);
                                startTimeText.setText(formatTime(current));
                                handler.postDelayed(this, 500);
                            }
                        }
                    };
                    handler.post(updateSeekBar);
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memutar lagu", Toast.LENGTH_SHORT).show();
            }
        } else {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void checkIfFavorite() {
        SupabaseFavoriteService service = RetrofitClient.getSupabaseInstance().create(SupabaseFavoriteService.class);
        service.getFavorites("eq." + userId, "eq." + songUrl).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavorite = !response.body().isEmpty();
                    btnFavorite.setSelected(isFavorite);
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                Log.e("SUPABASE_ERROR", "Gagal cek favorit: " + t.getMessage());
            }
        });
    }

    private void saveFavoriteToSupabase() {
        JsonObject data = new JsonObject();
        data.addProperty("user_id", userId);
        data.addProperty("title", title);
        data.addProperty("artist", artist);
        data.addProperty("cover_url", coverUrl);
        data.addProperty("song_url", songUrl);

        SupabaseFavoriteService service = RetrofitClient.getSupabaseInstance().create(SupabaseFavoriteService.class);
        service.addToFavorites(data).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful()) {
                    isFavorite = true;
                    btnFavorite.setSelected(true);
                    Toast.makeText(MusicPlayerActivity.this, "Ditambahkan ke Favorit", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MusicPlayerActivity.this, "Gagal menyimpan ke Favorit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                Toast.makeText(MusicPlayerActivity.this, "Gagal koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFavoriteFromSupabase() {
        SupabaseFavoriteService service = RetrofitClient.getSupabaseInstance().create(SupabaseFavoriteService.class);
        service.deleteFavorite("eq." + userId, "eq." + songUrl).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isFavorite = false;
                    btnFavorite.setSelected(false);
                    Toast.makeText(MusicPlayerActivity.this, "Dihapus dari Favorit", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MusicPlayerActivity.this, "Gagal menghapus dari Favorit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MusicPlayerActivity.this, "Gagal koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            if (updateSeekBar != null) handler.removeCallbacks(updateSeekBar);
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}