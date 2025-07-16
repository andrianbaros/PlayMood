package com.example.playmood.presenter;

import android.util.Log;

import com.example.playmood.model.MoodModel;
import com.example.playmood.view.MainView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

public class MainPresenter {

    private final MainView view;
    private final FaceDetector detector;

    public MainPresenter(MainView view) {
        this.view = view;

        // Konfigurasi deteksi wajah
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        // Buat detector dari ML Kit
        detector = FaceDetection.getClient(options);
    }

    // Proses gambar untuk mendeteksi wajah dan mood
    public void processImage(InputImage image) {
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.size() > 0) {
                        Face face = faces.get(0); // Ambil wajah pertama
                        String mood = MoodModel.getMoodFromFace(face);
                        Log.d("MainPresenter", "Mood terdeteksi: " + mood);
                        view.showDetectedMood(mood);
                    } else {
                        view.showError("Wajah tidak terdeteksi.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainPresenter", "Gagal deteksi wajah: " + e.getMessage());
                    view.showError("Gagal deteksi wajah: " + e.getMessage());
                });
    }

    // Tutup FaceDetector agar tidak memory leak
    public void stop() {
        detector.close();
    }

}