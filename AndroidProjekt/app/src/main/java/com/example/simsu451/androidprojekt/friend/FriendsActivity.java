package com.example.simsu451.androidprojekt.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;
import com.example.simsu451.androidprojekt.UsersActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {
    TextView tvFriendRequests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        tvFriendRequests = (TextView) findViewById(R.id.tvFriendRequests);
        getFriendRequestsAmount();

        ListView lvFriends = (ListView) findViewById(R.id.lvFriends);
        if (lvFriends == null) throw new AssertionError("lvFriends is null");
        FriendsAdapter friendsAdapter = new FriendsAdapter(this);
        lvFriends.setAdapter(friendsAdapter);


        tvFriendRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, RequestsActivity.class);
                startActivity(intent);
            }
        });

        Button usersButton = (Button) findViewById(R.id.usersButton);
        if (usersButton == null) throw new AssertionError("usersButton is null");
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });

    }


    private void getFriendRequestsAmount() {
        String url = Constants.URL + "get-friend-requests";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Users friendRequests = gson.fromJson(response, Users.class);
                        tvFriendRequests.setText(String.format("Friend requests: %s", friendRequests.getUsers().size()));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Token.getInstance().getToken());
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
}
