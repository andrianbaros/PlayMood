package com.example.playmood.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TokenResponse {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("expires_in")
    public int expiresIn;
}