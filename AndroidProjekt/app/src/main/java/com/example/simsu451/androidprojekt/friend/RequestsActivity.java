package com.example.simsu451.androidprojekt.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.simsu451.androidprojekt.R;



public class RequestsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        ListView lvRequests = (ListView) findViewById(R.id.lvRequests);
        if (lvRequests== null) throw new AssertionError("listView is null");

        RequestsAdapter requestsAdapter = new RequestsAdapter(this);
        lvRequests.setAdapter(requestsAdapter);

        Button friendsButton = (Button) findViewById(R.id.friendsButton);
        if (friendsButton == null) throw new AssertionError("friendsButton is null");
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



}
