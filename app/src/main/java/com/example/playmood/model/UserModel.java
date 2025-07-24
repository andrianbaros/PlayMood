package com.example.playmood.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private String uid;  // Tambahan UID
    private String name;
    private String email;
    private String username;
    private String password;
    private String profileImageUrl;


    // Constructor kosong
    public UserModel() {

        this.profileImageUrl = "";
    }

    // Constructor lengkap
    public UserModel(String uid, String name, String email, String username, String password) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profileImageUrl = "";
    }

    // Getter & Setter untuk uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter lainnya
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl != null ? profileImageUrl : "";
    }



    // Setters lainnya
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = (profileImageUrl != null) ? profileImageUrl : "";
    }


}
