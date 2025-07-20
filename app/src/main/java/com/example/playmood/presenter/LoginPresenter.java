package com.example.playmood.presenter;

import androidx.annotation.NonNull;

import com.example.playmood.model.UserModel;
import com.example.playmood.view.LoginView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
            loginView.onInputError("username", "Email/Username tidak boleh kosong");
            return;
        }
        if (password.isEmpty()) {
            loginView.onInputError("password", "Password tidak boleh kosong");
            return;
        }

        if (loginInput.contains("@")) {
            // Login menggunakan email langsung
            loginWithEmail(loginInput, password);
        } else {
            // Login via username: cari email berdasarkan username
            reference.orderByChild("username").equalTo(loginInput)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    UserModel user = child.getValue(UserModel.class);
                                    if (user != null && user.getEmail() != null) {
                                        loginWithEmail(user.getEmail(), password);
                                        return;
                                    }
                                }
                                loginView.onLoginError("Data pengguna tidak lengkap");
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

    private void loginWithEmail(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        // Ambil data berdasarkan UID (Users/{UID})
                        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserModel user = snapshot.getValue(UserModel.class);
                                if (user != null) {
                                    loginView.onLoginSuccess(user);
                                } else {
                                    loginView.onLoginError("Data pengguna tidak ditemukan");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                loginView.onLoginError("Error: " + error.getMessage());
                            }
                        });
                    } else {
                        loginView.onLoginError("Gagal mendapatkan UID setelah login");
                    }
                })
                .addOnFailureListener(e ->
                        loginView.onLoginError("Login gagal: " + e.getMessage()));
    }
}