package com.example.playmood.presenter;

import com.example.moodmusicapp.model.MoodModel;
import com.example.moodmusicapp.view.MainView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

public class MainPresenter {

    private final MainView view;
    private final FaceDetector detector;

    public MainPresenter(MainView view) {
        this.view = view;

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        detector = FaceDetection.getClient(options);
    }

    public void processImage(InputImage image) {
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.size() > 0) {
                        String mood = MoodModel.getMoodFromFace(faces.get(0));
                        view.showDetectedMood(mood);
                    } else {
                        view.showError("Wajah tidak terdeteksi.");
                    }
                })
                .addOnFailureListener(e -> view.showError("Gagal deteksi wajah: " + e.getMessage()));
    }
}