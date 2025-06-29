package com.example.playmood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playmood.presenter.LoginPresenter;
import com.example.playmood.view.LoginView;

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

        loginButton.setOnClickListener(v -> {
            String username = loginUsername.getText().toString();
            String password = loginPassword.getText().toString();
            loginPresenter.login(username, password);
        });

        signupRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(this, ResetPasswordActivity.class));
        });
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
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
