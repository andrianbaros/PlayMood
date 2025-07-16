package com.example.playmood.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Untuk AUTH (token)
    private static final String AUTH_BASE_URL = "https://accounts.spotify.com/";
    private static Retrofit authRetrofit;

    public static Retrofit getAuthInstance() {
        if (authRetrofit == null) {
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(AUTH_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return authRetrofit;
    }

    // Untuk Spotify API (ambil track, playlist, dll)
    private static final String API_BASE_URL = "https://api.spotify.com/";
    private static Retrofit apiRetrofit;

    public static Retrofit getSpotifyApiInstance() {
        if (apiRetrofit == null) {
            apiRetrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return apiRetrofit;
    }
}