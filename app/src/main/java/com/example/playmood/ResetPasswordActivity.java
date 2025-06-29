package com.example.playmood;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;
    private FirebaseAuth auth;
    private static final String TAG = "ResetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        auth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                emailInput.setError("Email tidak boleh kosong");
                emailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Format email tidak valid");
                emailInput.requestFocus();
                return;
            }

            // Kirim email reset password
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email reset berhasil dikirim ke: " + email);
                            Toast.makeText(this, "Link reset password telah dikirim ke email", Toast.LENGTH_LONG).show();
                            finish(); // Kembali ke Login
                        } else {
                            String errorMessage = (task.getException() != null)
                                    ? task.getException().getMessage()
                                    : "Terjadi kesalahan saat mengirim email";
                            Log.e(TAG, "Gagal mengirim email reset: " + errorMessage);
                            Toast.makeText(this, "Gagal mengirim email: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
