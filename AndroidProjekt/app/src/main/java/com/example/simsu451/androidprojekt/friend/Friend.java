package com.example.simsu451.androidprojekt.friend;

/**
 * Created by simsu451 on 12/05/17.
 */

public class Friend {
    private String name;
    private String email;
    private boolean isFriend;

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

}
