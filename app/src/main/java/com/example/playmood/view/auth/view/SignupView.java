package com.example.playmood.view.auth.view;

public interface SignupView {
    void onSignUpSuccess();
    void onSignUpError(String message);
    void onInputError(String field, String message);
}
