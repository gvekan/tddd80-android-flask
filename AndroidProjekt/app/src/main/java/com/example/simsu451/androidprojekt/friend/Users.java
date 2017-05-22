package com.example.simsu451.androidprojekt.friend;

import java.util.List;

/**
 * Created by simsu451 on 12/05/17.
 */

public class Users {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addUsers(List<User> users) {
        this.users.addAll(users);
    }
}
