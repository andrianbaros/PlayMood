package com.example.playmood.presenter;

import android.util.Log;

import com.example.playmood.model.AlbumModel;
import com.example.playmood.model.SupabaseService;
import com.example.playmood.presenter.contract.HomeContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomePresenter implements HomeContract.Presenter {

    private final HomeContract.View view;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void onHomeLoaded() {
        view.showWelcomeMessage("Selamat datang di Home!");

        // Inisialisasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ebtorprhvaefmogaxhyq.supabase.co/") // Ganti dengan base URL Supabase kamu
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseService service = retrofit.create(SupabaseService.class);

        // Panggil endpoint Supabase
        Call<List<AlbumModel>> call = service.getAlbums();
        call.enqueue(new Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AlbumModel> albumList = response.body();
                    view.showAlbums(albumList);
                } else {
                    Log.e("SUPABASE", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<AlbumModel>> call, Throwable t) {
                Log.e("SUPABASE", "Request failed: " + t.getMessage(), t);
            }
        });
    }
}
