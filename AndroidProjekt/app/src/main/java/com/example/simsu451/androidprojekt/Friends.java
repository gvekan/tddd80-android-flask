package com.example.simsu451.androidprojekt;

import java.util.List;

/**
 * Created by simsu451 on 12/05/17.
 */

public class Friends {

    private List<Friend> friends;

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public void addFriends(List<Friend> friends) {
        this.friends.addAll(friends);
    }
}
