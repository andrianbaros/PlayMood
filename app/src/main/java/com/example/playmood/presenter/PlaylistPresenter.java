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
                keywords = happyKeywords; break;
            case "sad":
                keywords = sadKeywords; break;
            case "neutral":
                keywords = neutralKeywords; break;
            case "angry":
                keywords = angryKeywords; break;
            case "surprised":
                keywords = surprisedKeywords; break;
            default:
                keywords = new String[]{"popular", "vibes", "chill"}; break;
        }

        String query = keywords[new Random().nextInt(keywords.length)];

        Call<TrackResponse> call = trackService.searchTracks("Bearer " + token, query, "track", 15);
        call.enqueue(new Callback<TrackResponse>() {
            @Override
            public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TrackItem> tracks = response.body().tracks.items;
                    if (tracks != null && !tracks.isEmpty()) {
                        view.onTracksReceived(tracks);
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
}
