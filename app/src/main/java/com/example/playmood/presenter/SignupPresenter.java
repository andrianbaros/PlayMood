package com.example.playmood.presenter;

import androidx.annotation.NonNull;

import com.example.playmood.model.UserModel;
import com.example.playmood.view.SignupView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupPresenter {
    private final SignupView signupView;
    private final FirebaseAuth auth;
    private final DatabaseReference database;

    public SignupPresenter(SignupView signupView) {
        this.signupView = signupView;
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance().getReference("Users");
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
        if (password.length() < 6) {
            signupView.onInputError("password", "Password minimal 6 karakter");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        // Buat objek user dan simpan ke Realtime Database
                        UserModel user = new UserModel(name, email, username, password);
                        database.child(username).setValue(user)
                                .addOnSuccessListener(unused -> signupView.onSignUpSuccess())
                                .addOnFailureListener(e -> signupView.onSignUpError("Gagal menyimpan data: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> signupView.onSignUpError("Gagal daftar: " + e.getMessage()));
    }
}
