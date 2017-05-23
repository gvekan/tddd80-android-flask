package com.example.simsu451.androidprojekt;

/**
 * The token class is used to check if your session is valid.
 */

public class Token {
    private static final Token instance = new Token();
    private String token = null;

    private Token() {
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static Token getInstance() {
        return instance;
    }
}
