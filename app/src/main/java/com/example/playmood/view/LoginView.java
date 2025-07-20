package com.example.playmood.view;

import com.example.playmood.model.UserModel;
import com.example.playmood.presenter.LoginPresenter;

public interface LoginView {
    void onLoginError(String message);
    void onUserNotFound();
    void onInputError(String field, String message);
    void onLoginSuccess(UserModel user);

}
