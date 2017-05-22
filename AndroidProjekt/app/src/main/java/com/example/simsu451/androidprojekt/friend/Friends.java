package com.example.simsu451.androidprojekt.friend;

import com.example.simsu451.androidprojekt.friend.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simsu451 on 12/05/17.
 */

public class Friends {

    private ArrayList<Friend> friends;

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public void addFriends(ArrayList<Friend> friends) {
        this.friends.addAll(friends);
    }
}
