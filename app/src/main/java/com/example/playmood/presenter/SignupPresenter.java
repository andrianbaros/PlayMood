package com.example.playmood.presenter;

import androidx.annotation.NonNull;

import com.example.playmood.model.UserModel;
import com.example.playmood.view.SignupView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupPresenter {
    private SignupView signupView;
    private DatabaseReference reference;

    public SignupPresenter(SignupView signupView) {
        this.signupView = signupView;
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void signup(String name, String email, String username, String password) {
        if (name.isEmpty()) {
            signupView.onInputError("name", "Nama tidak boleh kosong");
            return;
        }
        if (email.isEmpty()) {
            signupView.onInputError("email", "Email tidak boleh kosong");
            return;
        }
        if (username.isEmpty()) {
            signupView.onInputError("username", "Username tidak boleh kosong");
            return;
        }
        if (password.isEmpty()) {
            signupView.onInputError("password", "Password tidak boleh kosong");
            return;
        }

        UserModel user = new UserModel(name, email, username, password);
        reference.child(username).setValue(user)
                .addOnSuccessListener(unused -> signupView.onSignUpSuccess())
                .addOnFailureListener(e -> signupView.onSignUpError("Gagal signup: " + e.getMessage()));
    }
}