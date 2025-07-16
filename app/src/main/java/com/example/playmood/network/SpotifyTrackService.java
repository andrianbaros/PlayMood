package com.example.playmood.network;

import com.example.playmood.model.TrackResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SpotifyTrackService {

    @GET("v1/search")
    Call<TrackResponse> searchTracks(
            @Header("Authorization") String authHeader,
            @Query("q") String query,
            @Query("type") String type,
            @Query("limit") int limit
    );
}