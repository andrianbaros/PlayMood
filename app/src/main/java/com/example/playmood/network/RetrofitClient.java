package com.example.playmood.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // === 1. Spotify AUTH ===
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

    // === 2. Spotify API ===
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

    // === 3. Supabase ===
    private static final String SUPABASE_BASE_URL = "https://ebtorprhvaefmogaxhyq.supabase.co/rest/v1/";

    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVidG9ycHJodmFlZm1vZ2F4aHlxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTIzMjc1ODYsImV4cCI6MjA2NzkwMzU4Nn0.IM78fjAk7ZhX2-Ihhq6vfpduhtIPgdnKLeQGrSrI-hg";

    private static Retrofit supabaseRetrofit;

    public static Retrofit getSupabaseInstance() {
        if (supabaseRetrofit == null) {

            // Logging Interceptor (untuk debugging request/response)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request request = original.newBuilder()
                                    .header("apikey", SUPABASE_ANON_KEY)
                                    .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            supabaseRetrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return supabaseRetrofit;
    }
}