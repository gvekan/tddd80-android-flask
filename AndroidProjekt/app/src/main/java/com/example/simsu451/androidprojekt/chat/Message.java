package com.example.simsu451.androidprojekt.chat;

/**
 * Created by simsu451 on 10/05/17.
 */

public class Message {
    private int id;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
