package com.example.playmood.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface SupabaseService {
    @Headers({
            "apikey: sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD",
            "Authorization: Bearer sb_secret_pPtrqnOgYBpKT-erW4dK0w_HgY1sLuD "
    })
    @GET("rest/v1/playmood")
    Call<List<AlbumModel>> getAlbums();
}