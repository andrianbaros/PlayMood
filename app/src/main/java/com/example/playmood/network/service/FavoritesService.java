package com.example.playmood.network.service;

import com.example.playmood.model.FavoriteModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FavoritesService {

    @GET("favorites")
    Call<List<FavoriteModel>> getFavoritesByUser(
            @Query("user_id") String userIdEq,      // nilai: "eq.UID"
            @Query("select") String selectFields    // nilai: "*"
    );
}
