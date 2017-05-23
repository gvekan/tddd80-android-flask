package com.example.simsu451.androidprojekt.chat;

/**
 * Java bean for a message
 */

class Message {
    private int index;
    private String text;
    private String sentBy;

    int getIndex() {
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

    String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
