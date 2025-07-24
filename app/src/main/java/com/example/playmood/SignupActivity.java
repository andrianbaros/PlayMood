package com.example.playmood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playmood.R;
import com.example.playmood.presenter.SignupPresenter;
import com.example.playmood.view.SignupView;

public class SignupActivity extends AppCompatActivity implements SignupView {

    EditText signupName, signupEmail, signupUsername, signupPassword;
    Button signupButton;
    TextView loginRedirectText;

    SignupPresenter signupPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupPresenter = new SignupPresenter(this);

        signupButton.setOnClickListener(v -> {
            String name = signupName.getText().toString();
            String email = signupEmail.getText().toString();
            String username = signupUsername.getText().toString();
            String password = signupPassword.getText().toString();

            signupPresenter.signup(name, email, username, password);
        });

        loginRedirectText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(this, "Signup berhasil!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSignUpError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInputError(String field, String message) {
        switch (field) {
            case "name":
                signupName.setError(message);
                break;
            case "email":
                signupEmail.setError(message);
                break;
            case "username":
                signupUsername.setError(message);
                break;
            case "password":
                signupPassword.setError(message);
                break;
            case "profileImageUrl":

        }
    }
}