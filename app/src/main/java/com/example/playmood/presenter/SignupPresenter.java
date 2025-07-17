package com.example.playmood.presenter;

import androidx.annotation.NonNull;

import com.example.playmood.model.UserModel;
import com.example.playmood.view.SignupView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;

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
        // Validasi password: minimal 8 karakter, campuran huruf dan angka
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (!Pattern.matches(regex, password)) {
            signupView.onInputError("password",
                    "Password minimal 8 karakter dan harus mengandung huruf & angka");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        UserModel user = new UserModel(name, email, username, password);
                        database.child(username).setValue(user)
                                .addOnSuccessListener(unused -> signupView.onSignUpSuccess())
                                .addOnFailureListener(e ->
                                        signupView.onSignUpError("Gagal menyimpan data: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e ->
                        signupView.onSignUpError("Gagal daftar: " + e.getMessage()));
    }

}
