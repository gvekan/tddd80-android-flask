package com.example.simsu451.androidprojekt;

/**
 * Created by gusan092 on 10/05/17.
 */

public class TokenInstance {
    private static final TokenInstance instance = new TokenInstance();
    private String token = null;

    private TokenInstance() {
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static TokenInstance getInstance() {
        return instance;
    }
}
