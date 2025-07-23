package com.example.playmood.network;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseFavoriteService {

    @Headers({
            "apikey: sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Authorization: Bearer sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("favorites")
    Call<List<JsonObject>> addToFavorites(@Body JsonObject body);

    @Headers({
            "apikey: sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Authorization: Bearer sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD"
    })
    @GET("favorites")
    Call<List<JsonObject>> getFavorites(
            @Query(value = "user_id", encoded = true) String userId,
            @Query(value = "song_url", encoded = true) String songUrl
    );

    @Headers({
            "apikey: sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Authorization: Bearer sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD"
    })
    @DELETE("favorites")
    Call<Void> deleteFavorite(
            @Query("user_id") String userIdFilter,
            @Query("song_url") String songUrlFilter
    );
}
