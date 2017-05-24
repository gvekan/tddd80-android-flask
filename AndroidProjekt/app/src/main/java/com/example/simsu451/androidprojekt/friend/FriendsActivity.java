package com.example.simsu451.androidprojekt.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.LoginActivity;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;
import com.example.simsu451.androidprojekt.UsersActivity;
import com.example.simsu451.androidprojekt.user.ProfileActivity;
import com.example.simsu451.androidprojekt.wall.WallActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The FriendActivity is the activity where you can see all your friends.
 */

public class FriendsActivity extends AppCompatActivity {
    private Button requestsButton;
    FriendsAdapter friendsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        setTitle("Friends");

        requestsButton = (Button) findViewById(R.id.requestsButton);
        getFriendRequestsAmount();

        ListView lvFriends = (ListView) findViewById(R.id.lvFriends);
        if (lvFriends == null) throw new AssertionError("lvFriends is null");
        friendsAdapter = new FriendsAdapter(this);
        lvFriends.setAdapter(friendsAdapter);


        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, RequestsActivity.class);
                startActivity(intent);
            }
        });

        Button profileButton = (Button) findViewById(R.id.profileButton);
        if (profileButton == null) throw new AssertionError("profileButton is null");
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button wallButton = (Button) findViewById(R.id.wallButton);
        if (wallButton == null) throw new AssertionError("wallButton is null");
        wallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsActivity.this, WallActivity.class);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        friendsAdapter.getFriends();
        getFriendRequestsAmount();
    }

    private void getFriendRequestsAmount() {
        String url = Constants.URL + "get-friend-request-amount";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse;
                        try {
                            jsonResponse = new JSONObject(response);
                            int amount = jsonResponse.getInt("amount");
                            requestsButton.setText(String.format("Friend requests: %s", amount));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(FriendsActivity.this, new Bundle());
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
