package com.example.playmood.presenter.contract;

import com.example.playmood.model.AlbumModel;

import java.util.List;

public interface HomeContract {
    interface View {
        void showAlbums(List<AlbumModel> albums);

        void showWelcomeMessage(String message);
    }

    interface Presenter {
        void onHomeLoaded();
    }

    void showAlbums(List<AlbumModel> albums);

}
