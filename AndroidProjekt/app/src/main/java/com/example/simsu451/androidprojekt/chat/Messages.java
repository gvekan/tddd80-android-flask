package com.example.simsu451.androidprojekt.chat;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Messages is a java bean for a list of <Message>
 */

class Messages {
    private ArrayList<Message> messages;

    ArrayList<Message> getMessages() {
        return messages;
    }

    void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
        Collections.sort(this.messages, new MessageComparator());
    }

    void addMessages(ArrayList<Message> messages) {
        this.messages.addAll(messages);
        Collections.sort(this.messages, new MessageComparator());
    }

    int getLatest() {
        int size = messages.size();
        if (size == 0) return -1;
        return messages.get(size - 1).getIndex();
    }

    int getOldest() {
        if (messages.size() == 0) return -1;
        return messages.get(0).getIndex();
    }
}
