package com.example.simsu451.androidprojekt.user;

/**
 * Created by gusan092 on 10/05/17.
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
