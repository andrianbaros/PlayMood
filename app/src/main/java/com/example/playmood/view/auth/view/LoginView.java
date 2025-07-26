package com.example.playmood.view.auth.view;

import com.example.playmood.model.UserModel;

public interface LoginView {
    void onLoginError(String message);
    void onUserNotFound();
    void onInputError(String field, String message);
    void onLoginSuccess(UserModel user);

}
