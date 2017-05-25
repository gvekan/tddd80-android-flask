package com.example.simsu451.androidprojekt.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
import com.example.simsu451.androidprojekt.chat.ChatActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * The FriendAdapter handles the FriendActivity. It handles the list of Friends the friend has.
 */

class FriendsAdapter extends ArrayAdapter<User> {
    private Users users = new Users();
    FriendsAdapter(Context context) {
        super(context, R.layout.activity_friends);
        getFriends();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend, parent, false);
        }
        final User user = getItem(position);
        if (user != null) {
            TextView tvFriend = (TextView) convertView.findViewById(R.id.tvUser);
            tvFriend.setText(user.getFirstName() + ' ' + user.getLastName());
            Button removeButton = (Button) convertView.findViewById(R.id.removeButton);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFriend(user);
                }
            });

            Button chatButton = (Button) convertView.findViewById(R.id.chatButton);
            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("friend", new Gson().toJson(user)); // http://stackoverflow.com/questions/4249897/how-to-send-objects-through-bundle
                    getContext().startActivity(intent);
                }
            });
        }
        return convertView;
    }


    void getFriends() {
        String url = Constants.URL + "get-friends";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        clear();
                        users = gson.fromJson(response, Users.class);
                        addAll(users.getUsers());
                        notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(getContext(), new Bundle());
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
    private void removeFriend(final User user) {
        String url = Constants.URL + "remove-friend/" + user.getEmail();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        clear();
                        users.getUsers().remove(user);
                        users.setUsers(users.getUsers());
                        addAll(users.getUsers());
                        notifyDataSetChanged();

                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(getContext(), new Bundle());
                    }
                }
        ){
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
