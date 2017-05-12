package com.example.simsu451.androidprojekt.chat;

import com.example.simsu451.androidprojekt.chat.Message;

import java.util.Collections;
import java.util.List;

/**
 * Created by simsu451 on 13/05/17.
 */

public class Messages {
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        Collections.sort(this.messages, new MessageComparator());
    }

    public void addMessages(List<Message> messages) {
        this.messages.addAll(messages);
        Collections.sort(this.messages, new MessageComparator());
    }
}
