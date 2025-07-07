package com.example.playmood.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private String name;
    private String email;
    private String username;
    private String password;

    // Tambahan untuk fitur Follow
    private Map<String, Boolean> followers;
    private Map<String, Boolean> following;


    public UserModel() {}

    public UserModel(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.followers = new HashMap<>();
        this.following = new HashMap<>();
    }

    // Getter
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Map<String, Boolean> getFollowers() { return followers; }
    public Map<String, Boolean> getFollowing() { return following; }

    // Setter
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFollowers(Map<String, Boolean> followers) { this.followers = followers; }
    public void setFollowing(Map<String, Boolean> following) { this.following = following; }
}
