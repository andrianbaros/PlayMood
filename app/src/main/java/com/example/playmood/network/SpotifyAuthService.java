package com.example.playmood.network;

import com.example.playmood.model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SpotifyAuthService {

    @FormUrlEncoded
    @POST("api/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<TokenResponse> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );
}