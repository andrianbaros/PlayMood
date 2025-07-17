package com.example.playmood.presenter;

import androidx.annotation.NonNull;

import com.example.playmood.model.UserModel;
import com.example.playmood.view.LoginView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class LoginPresenter {
    private final LoginView loginView;
    private final DatabaseReference reference;
    private final FirebaseAuth firebaseAuth;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        this.reference = FirebaseDatabase.getInstance().getReference("Users");
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void login(String loginInput, String password) {
        if (loginInput.isEmpty()) {
            loginView.onInputError("login", "Email/Username tidak boleh kosong");
            return;
        }
        if (password.isEmpty()) {
            loginView.onInputError("password", "Password tidak boleh kosong");
            return;
        }

        // Tentukan apakah loginInput adalah email atau username
        if (loginInput.contains("@")) {
            // Loginnya langsung via email
            firebaseAuth.signInWithEmailAndPassword(loginInput, password)
                    .addOnSuccessListener(auth -> loginView.onLoginSuccess())
                    .addOnFailureListener(e ->
                            loginView.onLoginError("Login gagal: " + e.getMessage()));
        } else {
            // Input adalah username
            Query query = reference.orderByChild("username").equalTo(loginInput);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        DataSnapshot data = snapshot.getChildren().iterator().next();
                        UserModel user = data.getValue(UserModel.class);
                        if (user != null) {
                            String email = user.getEmail();
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(auth -> loginView.onLoginSuccess())
                                    .addOnFailureListener(e ->
                                            loginView.onLoginError("Login gagal: " + e.getMessage()));
                        } else {
                            loginView.onLoginError("Data pengguna rusak");
                        }
                    } else {
                        loginView.onUserNotFound();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loginView.onLoginError("Error: " + error.getMessage());
                }
            });
        }
    }

}
