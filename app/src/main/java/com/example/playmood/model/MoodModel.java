package com.example.playmood.model;

import com.google.mlkit.vision.face.Face;

public class MoodModel {
    public static String getMoodFromFace(Face face) {
        Float smileProb = face.getSmilingProbability();
        if (smileProb != null) {
            if (smileProb > 0.6) return "Happy";
            else if (smileProb < 0.3) return "Sad";
            else return "Neutral";
        }
        return "Unknown";
    }
}