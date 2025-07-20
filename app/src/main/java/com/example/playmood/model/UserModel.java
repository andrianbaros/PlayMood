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

    private Map<String, Boolean> followers;
    private Map<String, Boolean> following;

    // Constructor kosong
    public UserModel() {
        this.followers = new HashMap<>();
        this.following = new HashMap<>();
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
        this.followers = new HashMap<>();
        this.following = new HashMap<>();
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

    public Map<String, Boolean> getFollowers() {
        return followers != null ? followers : new HashMap<>();
    }

    public Map<String, Boolean> getFollowing() {
        return following != null ? following : new HashMap<>();
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

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = (followers != null) ? followers : new HashMap<>();
    }

    public void setFollowing(Map<String, Boolean> following) {
        this.following = (following != null) ? following : new HashMap<>();
    }
}
