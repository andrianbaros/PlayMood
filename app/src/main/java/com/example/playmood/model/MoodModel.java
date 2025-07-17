package com.example.playmood.model;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;




public class MoodModel {
    public static String getMoodFromFace(Face face) {
        Float smileProb = face.getSmilingProbability();
        Float leftEyeOpen = face.getLeftEyeOpenProbability();
        Float rightEyeOpen = face.getRightEyeOpenProbability();
        float rotX = face.getHeadEulerAngleX(); // atas-bawah
        float rotY = face.getHeadEulerAngleY(); // kiri-kanan

        if (smileProb != null) {
            if (smileProb > 0.6f) {
                return "Happy";
            } else if (smileProb < 0.3f) {
                if (leftEyeOpen != null && rightEyeOpen != null &&
                        leftEyeOpen < 0.3f && rightEyeOpen < 0.3f) {
                    return "Angry"; // ekspresi marah: tidak senyum + mata menyempit
                } else {
                    return "Sad";
                }
            } else {
                if (Math.abs(rotX) > 15 || Math.abs(rotY) > 20) {
                    return "Surprised"; // kepala miring + ekspresi netral = terkejut
                } else {
                    return "Neutral";
                }
            }
        }

        return "Unknown";
    }
}