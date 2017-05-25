package com.example.simsu451.androidprojekt.friend;

import android.content.Context;
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
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * The RequestAdapter handles the list of your friend requests.
 */

class RequestsAdapter extends ArrayAdapter<User> {
    private Users friendRequests = new Users();
    RequestsAdapter(Context context) {
        super(context, R.layout.activity_friend_requests);
        getFriendRequests();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request, parent, false);
        }
        final User user = getItem(position);
        if (user != null) {
            TextView tvUser = (TextView) convertView.findViewById(R.id.tvUser);
            tvUser.setText(user.getFirstName() + ' ' + user.getLastName());
            Button acceptButton = (Button) convertView.findViewById(R.id.acceptButton);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriend(user);
                }
            });
            Button removeButton = (Button) convertView.findViewById(R.id.removeButton);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeFriendRequest(user);
                }
            });
        }
        return convertView;
    }

    private void getFriendRequests() {
        String url = Constants.URL + "get-friend-requests";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        clear();
                        friendRequests = gson.fromJson(response, Users.class);
                        addAll(friendRequests.getUsers());
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
    private void addFriend(final User user) {
        String url = Constants.URL + "accept-friend-request/" + user.getEmail();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        remove(user);
                        friendRequests.getUsers().remove(user);
                        friendRequests.setUsers(friendRequests.getUsers());
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

    private void removeFriendRequest(final User user) {
        String url = Constants.URL + "remove-friend-request/" + user.getEmail();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        clear();
                        friendRequests.getUsers().remove(user);
                        friendRequests.setUsers(friendRequests.getUsers());
                        addAll(friendRequests.getUsers());
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
