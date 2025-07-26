package com.example.playmood.network.service;

import com.example.playmood.model.AlbumModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
;

public interface SupabaseService {

    @Headers({
            "apikey: sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Authorization: Bearer sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Content-Type: application/json"
    })
    @GET("rest/v1/playmood") // benar
    Call<List<AlbumModel>> getAlbums(@Query("select") String select);

}