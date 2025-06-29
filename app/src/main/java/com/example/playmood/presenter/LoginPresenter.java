package com.example.playmood.presenter;
import androidx.annotation.NonNull;

import com.example.playmood.model.UserModel;
import com.example.playmood.view.LoginView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginPresenter {
    private LoginView loginView;
    private DatabaseReference reference;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void login(String username, String password) {
        if (username.isEmpty()) {
            loginView.onInputError("username", "Username tidak boleh kosong");
            return;
        }
        if (password.isEmpty()) {
            loginView.onInputError("password", "Password tidak boleh kosong");
            return;
        }

        Query checkUser = reference.orderByChild("username").equalTo(username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        UserModel user = userSnap.getValue(UserModel.class);
                        if (user != null && user.getPassword().equals(password)) {
                            loginView.onLoginSuccess();
                        } else {
                            loginView.onLoginError("Password salah");
                        }
                    }
                } else {
                    loginView.onUserNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loginView.onLoginError("Database error: " + error.getMessage());
            }
        });
    }
}
