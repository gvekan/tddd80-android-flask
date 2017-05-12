package com.example.simsu451.androidprojekt.chat;

import java.util.Collection;
import java.util.Comparator;

/**
 * Created by simsu451 on 13/05/17.
 */

public class MessageComparator implements Comparator<Message> {
    @Override
    public int compare(Message lhs, Message rhs) {
        if (lhs.getId() > rhs.getId()) return 1;
        else if (lhs.getId() < rhs.getId()) return -1;
        return 0;
    }
}
