package com.example.playmood;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playmood.presenter.LoginPresenter;
import com.example.playmood.view.LoginView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class LoginActivity extends AppCompatActivity implements LoginView {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText, forgotPasswordText;

    LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.Login_Username);
        loginPassword = findViewById(R.id.Login_Password);
        loginButton = findViewById(R.id.Login_Button);
        signupRedirectText = findViewById(R.id.signupRedicrectText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        loginPresenter = new LoginPresenter(this);


        new android.os.Handler().postDelayed(() -> {
            showBottomSheetDialog();
        }, 1500);

        loginButton.setOnClickListener(v -> {
            String username = loginUsername.getText().toString();
            String password = loginPassword.getText().toString();
            loginPresenter.login(username, password);
        });

        signupRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(
                R.layout.bottom_sheet_login_options,
                null
        );
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        Button btnLogin = bottomSheetView.findViewById(R.id.btnLogin);
        Button btnCreate = bottomSheetView.findViewById(R.id.btnCreate);


        btnLogin.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Silakan lanjut login", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });


        btnCreate.setOnClickListener(v -> {
            bottomSheetDialog.dismiss(); // Tutup popup dulu
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, RouterActivity.class));
        finish();
    }

    @Override
    public void onLoginError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserNotFound() {
        loginUsername.setError("User tidak ditemukan");
    }

    @Override
    public void onInputError(String field, String message) {
        if (field.equals("username")) {
            loginUsername.setError(message);
        } else if (field.equals("password")) {
            loginPassword.setError(message);
        }
    }
}
