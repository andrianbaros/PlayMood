package com.example.playmood;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.playmood.presenter.MainPresenter;
import com.example.playmood.view.MainView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

public class MainActivity extends AppCompatActivity implements MainView {

    private PreviewView previewView;
    private TextView txtMood;
    private MainPresenter presenter;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.previewView);
        txtMood = findViewById(R.id.txtMood);
        Button btnDetect = findViewById(R.id.btnDetectMood);

        presenter = new MainPresenter(this);
        startCamera();
//baros
        btnDetect.setOnClickListener(v -> captureImageAndDetect());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                showError("Gagal membuka kamera.");
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureImageAndDetect() {
        if (imageCapture == null) {
            showError("Kamera belum siap.");
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    @Override
                    public void onCaptureSuccess(ImageProxy imageProxy) {
                        if (imageProxy.getImage() != null) {
                            InputImage image = InputImage.fromMediaImage(
                                    imageProxy.getImage(),
                                    imageProxy.getImageInfo().getRotationDegrees()
                            );
                            presenter.processImage(image);
                        }
                        imageProxy.close();
                    }
                });
    }

    @Override
    public void showDetectedMood(String mood) {
        txtMood.setText("Mood: " + mood);
    }

    @Override
    public void showError(String message) {
        txtMood.setText("Error: " + message);
    }
}