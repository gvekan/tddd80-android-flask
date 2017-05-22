package com.example.simsu451.androidprojekt.chat;

/**
 * Created by simsu451 on 10/05/17.
 */

public class Message {
    private int index;
    private String text;
    private String sentBy;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
