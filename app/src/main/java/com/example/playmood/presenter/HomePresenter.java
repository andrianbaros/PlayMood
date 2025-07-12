package com.example.playmood.presenter;

import com.example.playmood.presenter.contract.HomeContract;

public class HomePresenter implements HomeContract.Presenter {

    private final HomeContract.View view;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void onHomeLoaded() {
        view.showWelcomeMessage("Selamat datang di Home!");
    }
}
