package com.example.simsu451.androidprojekt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * The UsersActivity is the activity where you can see all users that are not your friends
 */

public class UsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setTitle("Users");
        ListView lvUsers = (ListView) findViewById(R.id.lvUsers);
        if (lvUsers == null) throw new AssertionError("list");
        UsersAdapter usersAdapter = new UsersAdapter(this);
        lvUsers.setAdapter(usersAdapter);

    }

}
