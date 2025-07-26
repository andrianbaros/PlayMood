package com.example.playmood.view.user;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.example.playmood.R;
import com.example.playmood.presenter.MainPresenter;
import com.example.playmood.view.music.activity.PlaylistActivity;
import com.example.playmood.view.home.MainView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

public class CameraFragment extends Fragment implements MainView {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private PreviewView previewView;
    private TextView txtMood;
    private Button btnDetect;
    private ImageCapture imageCapture;
    private MainPresenter presenter;

    public CameraFragment() {
        // Konstruktor kosong
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        previewView = view.findViewById(R.id.previewView);
        txtMood = view.findViewById(R.id.txtMood);
        btnDetect = view.findViewById(R.id.btnDetectMood);

        presenter = new MainPresenter(this);

        // Cek dan minta izin kamera sebelum startCamera()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            startCamera();
        }

        btnDetect.setOnClickListener(v -> captureImageAndDetect());

        return view;
    }

    // Hasil dari request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Izin kamera diberikan", Toast.LENGTH_SHORT).show();
                startCamera();
            } else {
                showError("Izin kamera ditolak. Tidak bisa menggunakan kamera.");
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageCapture
                );
            } catch (Exception e) {
                showError("Gagal membuka kamera.");
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void captureImageAndDetect() {
        if (imageCapture == null) {
            showError("Kamera belum siap.");
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageCapturedCallback() {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                        if (imageProxy.getImage() != null) {
                            InputImage image = InputImage.fromMediaImage(
                                    imageProxy.getImage(),
                                    imageProxy.getImageInfo().getRotationDegrees()
                            );
                            presenter.processImage(image);
                        }
                        imageProxy.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        showError("Gagal mengambil gambar.");
                    }
                });
    }

    @Override
    public void showDetectedMood(String mood) {
        txtMood.setText("Mood: " + mood);

        requireActivity().runOnUiThread(() -> {
            Intent intent = new Intent(requireContext(), PlaylistActivity.class);
            intent.putExtra("mood", mood);
            startActivity(intent);
        });
    }

    @Override
    public void showError(String message) {
        txtMood.setText("Error: " + message);
    }
}
