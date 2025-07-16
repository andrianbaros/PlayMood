package com.example.playmood.presenter;



import com.example.playmood.model.TokenResponse;
import com.example.playmood.model.TrackItem;
import com.example.playmood.model.TrackResponse;
import com.example.playmood.network.RetrofitClient;
import com.example.playmood.network.SpotifyAuthService;
import com.example.playmood.network.SpotifyTrackService;
import com.example.playmood.view.PlaylistView;

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

        // Sesuaikan query pencarian berdasarkan mood
        String query;
        switch (mood.toLowerCase()) {
            case "happy":
                query = "happy upbeat party"; break;
            case "sad":
                query = "sad emotional heartbreak"; break;
            case "neutral":
                query = "calm chill relax"; break;
            case "angry":
                query = "angry rock metal"; break;
            default:
                query = "top hits"; break;
        }

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