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
import com.example.simsu451.androidprojekt.friend.FriendsActivity;
import com.example.simsu451.androidprojekt.friend.User;
import com.example.simsu451.androidprojekt.friend.Users;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
            if(hasSentFriendRequest(user)) {

            }
            final Button button = (Button) convertView.findViewById(R.id.button);
            button.setText(R.string.add_friend);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.setOnClickListener(null);
                    addFriend(user);
                }
            });
        }
        return convertView;
    }

    private void hasSentFriendRequest(User user) {
        String url = Constants.URL + "get-friend-request-amount";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse;
                        try {
                            jsonResponse = new JSONObject(response);
                            boolean requestSent = jsonResponse.getBoolean("amount");
                            addButton.setText(String.format("Friend requests: %s", amount));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(FriendsActivity.this);
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
                        //Ändra text till friend request sent
//                        remove(user);
//                        users.getUsers().remove(user);
//                        notifyDataSetChanged();
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
