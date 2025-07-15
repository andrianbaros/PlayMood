package com.example.playmood;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class MusicPlayerActivity extends AppCompatActivity {

    private ImageView albumArt;
    private TextView songTitle, songArtist, startTimeText, endTimeText;
    private ImageButton btnPlayPause;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    private String songUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        // Inisialisasi view
        albumArt = findViewById(R.id.albumArt);
        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        seekBar = findViewById(R.id.seekBar);
        startTimeText = findViewById(R.id.startTime);
        endTimeText = findViewById(R.id.endTime);

        // Ambil data dari intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String artist = intent.getStringExtra("ARTIST");
        String coverUrl = intent.getStringExtra("COVER");
        songUrl = intent.getStringExtra("SONGURL");

        // Tampilkan data
        songTitle.setText(title);
        songArtist.setText(artist);

        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.rounded_image_background)
                .centerCrop()
                .into(albumArt);

        // Tombol play/pause
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic();
            }
        });

        // SeekBar interaction
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Tombol kembali
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        // Kosongkan dulu tombol lain
        findViewById(R.id.btnNext).setOnClickListener(v -> {});
        findViewById(R.id.btnPrevious).setOnClickListener(v -> {});
        findViewById(R.id.btnShuffle).setOnClickListener(v -> {});
        findViewById(R.id.btnRepeat).setOnClickListener(v -> {});
    }

    private void playMusic() {
        isPlaying = true;
        btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());

                mediaPlayer.setDataSource(songUrl);
                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                    int duration = mediaPlayer.getDuration();
                    seekBar.setMax(duration);
                    endTimeText.setText(formatTime(duration));

                    updateSeekBar = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null) {
                                int current = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(current);
                                startTimeText.setText(formatTime(current));
                                handler.postDelayed(this, 500);
                            }
                        }
                    };
                    handler.postDelayed(updateSeekBar, 0);
                });
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.start();
        }
    }

    private void pauseMusic() {
        isPlaying = false;
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
