package com.example.simsu451.androidprojekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.simsu451.androidprojekt.friend.FriendsActivity;


public class UsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ListView lvUsers = (ListView) findViewById(R.id.lvUsers);
        if (lvUsers == null) throw new AssertionError("list");
        UsersAdapter usersAdapter = new UsersAdapter(this);
        lvUsers.setAdapter(usersAdapter);

        Button friendsButton = (Button) findViewById(R.id.friendsButton);
        if (friendsButton == null) throw new AssertionError("friendsButton is null");
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

    }



}
