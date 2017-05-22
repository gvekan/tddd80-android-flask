package com.example.simsu451.androidprojekt;

import android.content.Context;
import android.content.Intent;
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
import com.example.simsu451.androidprojekt.chat.ChatActivity;
import com.example.simsu451.androidprojekt.friend.User;
import com.example.simsu451.androidprojekt.friend.Users;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by simsu451 on 22/05/17.
 */

public class UsersAdapter extends ArrayAdapter<User>{
    private Users users = new Users();
    public UsersAdapter(Context context) {
        super(context, R.layout.activity_users);
        getUsers();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user, parent, false);
        }
        final User user = getItem(position);
        if (user != null) {
            TextView tvUser = (TextView) convertView.findViewById(R.id.tvUser);
            tvUser.setText(user.getFirstName() + ' ' + user.getLastName());

            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("user", new Gson().toJson(user)); // http://stackoverflow.com/questions/4249897/how-to-send-objects-through-bundle
                    getContext().startActivity(intent);
                }
            });

            Button button = (Button) convertView.findViewById(R.id.button);
            button.setText(R.string.add_friend);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriend(user);
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

    private void addFriend(User user) {
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
