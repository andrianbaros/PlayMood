package com.example.playmood.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface SupabaseService {

    @Headers({
            "apikey: sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Authorization: Bearer sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Content-Type: application/json"
    })
    @GET("rest/v1/playmood?select=*")
    Call<List<AlbumModel>> getAlbums();
}