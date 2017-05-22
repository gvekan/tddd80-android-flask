package com.example.simsu451.androidprojekt.chat;

import com.example.simsu451.androidprojekt.chat.Message;
import com.example.simsu451.androidprojekt.chat.Message;
import com.example.simsu451.androidprojekt.chat.MessageComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by simsu451 on 13/05/17.
 */

public class Messages {
    private ArrayList<Message> messages;

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
        Collections.sort(this.messages, new MessageComparator());
    }

    public void addMessages(ArrayList<Message> messages) {
        this.messages.addAll(messages);
        Collections.sort(this.messages, new MessageComparator());
    }

    public int getLatest() {
        int size = messages.size();
        if (size == 0) return -1;
        return messages.get(size - 1).getIndex();
    }

    public int getOldest() {
        if (messages.size() == 0) return -1;
        return messages.get(0).getIndex();
    }
}
