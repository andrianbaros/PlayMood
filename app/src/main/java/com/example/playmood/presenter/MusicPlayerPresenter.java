package com.example.playmood.presenter;

import android.net.Uri;
import android.util.Log;

import com.example.playmood.network.RetrofitClient;
import com.example.playmood.network.service.SupabaseFavoriteService;
import com.example.playmood.view.music.view.MusicPlayerView;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicPlayerPresenter {
    private final MusicPlayerView view;
    private final SupabaseFavoriteService service;

    public MusicPlayerPresenter(MusicPlayerView view) {
        this.view = view;
        this.service = RetrofitClient.getSupabaseInstance().create(SupabaseFavoriteService.class);
    }

    public void checkFavoriteStatus(String userId, String songUrl) {
        String encodedUrl = Uri.encode(songUrl);

        service.getFavorites(userId, "eq." + encodedUrl).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isFavorited = !response.body().isEmpty();
                    view.updateFavoriteUI(isFavorited);
                } else {
                    view.showMessage("Gagal memuat status favorit");
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                view.showMessage("Kesalahan koneksi");
                Log.e("SUPABASE_ERROR", "Cek favorit gagal: " + t.getMessage());
            }
        });
    }

    public void checkAndAddToFavorites(String userId, String songUrl, JsonObject data) {
        String encodedUrl = Uri.encode(songUrl);

        service.getFavorites(userId, "eq." + encodedUrl).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful()) {
                    List<JsonObject> result = response.body();
                    if (result != null && result.isEmpty()) {
                        // Belum ada -> tambahkan
                        addToFavorites(data);
                    } else {
                        view.showMessage("Lagu sudah ada di Favorit");
                    }
                } else {
                    view.showMessage("Gagal memeriksa favorit");
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                view.showMessage("Kesalahan saat memeriksa favorit");
                Log.e("SUPABASE_ERROR", "Gagal cek favorit: " + t.getMessage());
            }
        });
    }

    private void addToFavorites(JsonObject data) {
        service.addToFavorites(data).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful()) {
                    view.updateFavoriteUI(true);
                    view.showMessage("Berhasil ditambahkan ke favorit");
                } else {
                    view.showMessage("Gagal menambahkan ke favorit");
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                view.showMessage("Kesalahan koneksi saat menyimpan");
                Log.e("SUPABASE_ERROR", "Gagal simpan favorit: " + t.getMessage());
            }
        });
    }
}
