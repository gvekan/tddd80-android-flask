package com.example.simsu451.androidprojekt;

import android.content.Context;
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
import com.example.simsu451.androidprojekt.friend.User;
import com.example.simsu451.androidprojekt.friend.Users;
import com.google.gson.Gson;


import java.util.HashMap;
import java.util.Map;

/**
 * The UsersAdapter handles a list of <User> that are not your friends.
 */

public class UsersAdapter extends ArrayAdapter<User>{
    private Users users = new Users();
    public UsersAdapter(Context context) {
        super(context, R.layout.activity_users);
        getUsers();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user, parent, false);
        }
        final User user = getItem(position);
        if (user != null) {
            TextView tvUser = (TextView) convertView.findViewById(R.id.tvUser);
            tvUser.setText(user.getFirstName() + ' ' + user.getLastName());

            final Button addButton = (Button) convertView.findViewById(R.id.button);
            if(user.hasRequestSent()) {
                addButton.setText(R.string.request_sent);
                addButton.setOnClickListener(null);
            }
            else {addButton.setText(R.string.add_friend);}
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addButton.setOnClickListener(null);
                    addFriend(user);
                    addButton.setText(R.string.request_sent);
                }
            });
        }
        return convertView;
    }

    private void getUsers() {
        String url = Constants.URL + "get-all-users";
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
        String url = Constants.URL + "send-friend-request/" + user.getEmail();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
