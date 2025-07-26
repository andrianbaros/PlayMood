package com.example.playmood.view.home;

public interface MainView {
    /**
     * Menampilkan hasil mood yang terdeteksi ke UI.
     * @param mood Mood yang terdeteksi, misalnya "Happy", "Sad", dll.
     */
    void showDetectedMood(String mood);

    /**
     * Menampilkan pesan kesalahan ke UI jika terjadi error.
     * @param message Pesan error
     */
    void showError(String message);
}