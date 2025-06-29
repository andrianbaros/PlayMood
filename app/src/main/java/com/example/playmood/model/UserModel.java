package com.example.playmood.model;

public class UserModel {
    private String name;
    private String email;
    private String username;
    private String password;

    // 🔹 Diperlukan oleh Firebase
    public UserModel() {}

    public UserModel(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getter
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Setter
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
