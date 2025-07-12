package com.example.playmood.presenter.contract;

public interface HomeContract {
    interface View {
        void showWelcomeMessage(String message);
    }

    interface Presenter {
        void onHomeLoaded();
    }

}
