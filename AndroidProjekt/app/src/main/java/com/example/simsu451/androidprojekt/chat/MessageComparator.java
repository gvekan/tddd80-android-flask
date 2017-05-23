package com.example.simsu451.androidprojekt.chat;

import java.util.Comparator;

/**
 * MessageComparator sorts messages according to their index
 */

class MessageComparator implements Comparator<Message> {
    @Override
    public int compare(Message message1, Message message2) {
        if (message1.getIndex() > message2.getIndex()) return 1;
        else if (message1.getIndex() < message2.getIndex()) return -1;
        return 0;
    }
}
