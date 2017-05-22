package com.example.simsu451.androidprojekt.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }



}
