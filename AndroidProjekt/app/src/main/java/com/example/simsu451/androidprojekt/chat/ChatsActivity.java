package com.example.simsu451.androidprojekt.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.Friend;
import com.example.simsu451.androidprojekt.Friends;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {
    private Friends friends = new Friends();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        friends.setFriends(new ArrayList<Friend>());
        getFriends();

        ListView listView = (ListView) findViewById(R.id.lwChats);
        if (listView == null) throw new AssertionError("listView is null");

        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friends.getFriends()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
                intent.putExtra("friend", new Gson().toJson(friends.getFriends().get(position))); // http://stackoverflow.com/questions/4249897/how-to-send-objects-through-bundle
                startActivity(intent);
            }

        });

    }

    private void getFriends() {
        String url = Constants.URL + "get-friends";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        friends.addFriends(gson.fromJson(response, Friends.class).getFriends());
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
