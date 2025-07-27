package com.example.playmood.presenter;

import com.example.playmood.network.respone.TokenResponse;
import com.example.playmood.network.respone.TrackItem;
import com.example.playmood.network.respone.TrackResponse;
import com.example.playmood.network.RetrofitClient;
import com.example.playmood.network.service.SpotifyAuthService;
import com.example.playmood.network.service.SpotifyTrackService;
import com.example.playmood.view.music.view.PlaylistView;

import java.util.Random;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistPresenter {

    private final PlaylistView view;

    public PlaylistPresenter(PlaylistView view) {
        this.view = view;
    }

    public void fetchAccessToken(String clientId, String clientSecret) {
        SpotifyAuthService authService = RetrofitClient
                .getAuthInstance()
                .create(SpotifyAuthService.class);

        Call<TokenResponse> call = authService.getAccessToken(
                "client_credentials", clientId, clientSecret
        );

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().accessToken;
                    view.onTokenReceived(token);
                } else {
                    view.onError("Token gagal diambil. Kode: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                view.onError("Gagal koneksi ambil token: " + t.getMessage());
            }
        });
    }

    public void fetchTracks(String token, String mood) {
        SpotifyTrackService trackService = RetrofitClient
                .getSpotifyApiInstance()
                .create(SpotifyTrackService.class);

        String[] happyKeywords = {"fun", "dance", "joyful", "upbeat", "party"};
        String[] sadKeywords = {"slow", "melancholy", "broken", "emotional", "lonely"};
        String[] neutralKeywords = {"lofi", "chill", "ambient", "relaxing", "instrumental"};
        String[] angryKeywords = {"rock", "metal", "aggressive", "screamo", "grunge"};
        String[] surprisedKeywords = {"edm", "exciting", "drop", "electronic", "intense"};

        String[] keywords;
        switch (mood.toLowerCase()) {
            case "happy":
                keywords = happyKeywords;
                break;
            case "sad":
                keywords = sadKeywords;
                break;
            case "neutral":
                keywords = neutralKeywords;
                break;
            case "angry":
                keywords = angryKeywords;
                break;
            case "surprised":
                keywords = surprisedKeywords;
                break;
            default:
                keywords = new String[]{"popular", "vibes", "chill"};
                break;
        }

        // Gabungkan 3 kata acak dari keywords menjadi query
        StringBuilder queryBuilder = new StringBuilder();
        Random rand = new Random();
        int count = 3; // jumlah kata untuk digabungkan

        for (int i = 0; i < count; i++) {
            String word = keywords[rand.nextInt(keywords.length)];
            queryBuilder.append(word);
            if (i < count - 1) {
                queryBuilder.append(" ");
            }
        }

        String query = queryBuilder.toString();

        Call<TrackResponse> call = trackService.searchTracks("Bearer " + token, query, "track", 15);
        call.enqueue(new Callback<TrackResponse>() {
            @Override
            public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TrackItem> tracks = response.body().tracks.items;
                    if (tracks != null && !tracks.isEmpty()) {
                        // Filter hasil berdasarkan judul agar tidak ambigu
                        List<TrackItem> filteredTracks = filterTracksByMood(tracks, mood);
                        if (!filteredTracks.isEmpty()) {
                            view.onTracksReceived(filteredTracks);
                        } else {
                            view.onError("Lagu tidak sesuai mood ditemukan: " + mood);
                        }
                    } else {
                        view.onError("Tidak ada lagu ditemukan untuk mood: " + mood);
                    }
                } else {
                    view.onError("Gagal ambil lagu. Kode: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TrackResponse> call, Throwable t) {
                view.onError("Gagal koneksi ambil lagu: " + t.getMessage());
            }
        });
    }

    // Fungsi untuk filter hasil berdasarkan keyword dalam mood
    private List<TrackItem> filterTracksByMood(List<TrackItem> tracks, String mood) {
        String[] negativeWords = {"die", "death", "cry", "lonely", "sad", "pain", "broken", "melancholy"};
        String[] positiveWords = {"smile", "joy", "happy", "cheer", "fun", "bright"};

        return tracks.stream()
                .filter(track -> {
                    String title = track.name.toLowerCase();

                    switch (mood.toLowerCase()) {
                        case "happy":
                            // Hindari lagu dengan kata negatif
                            for (String neg : negativeWords) {
                                if (title.contains(neg)) return false;
                            }
                            break;
                        case "sad":
                            // Hindari lagu dengan kata terlalu ceria
                            for (String pos : positiveWords) {
                                if (title.contains(pos)) return false;
                            }
                            break;
                        // Kamu bisa tambahkan mood lain juga di sini
                    }

                    return true;
                })
                .toList();
    }

}
