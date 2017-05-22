package com.example.simsu451.androidprojekt.user;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.simsu451.androidprojekt.friend.FriendsActivity;
import com.example.simsu451.androidprojekt.friend.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity{
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvCity;
    private TextView tvEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvLastName = (TextView) findViewById(R.id.tvLastName);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        getProfileInfo();

        Button friendsButton = (Button) findViewById(R.id.friendsButton);
        if (friendsButton == null) throw new AssertionError("friendsButton is null");
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });


        Button logoutButton = (Button) findViewById(R.id.logoutButton);

        if (logoutButton == null) throw new AssertionError("logoutButton is null");
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getProfileInfo() {
        String url = Constants.URL + "get-profile-info";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                User user = gson.fromJson(response, User.class);
                tvFirstName.setText(String.format("First name: %s", user.getFirstName()));
                tvLastName.setText(String.format("Last name: %s", user.getLastName()));
                tvCity.setText(String.format("City: %s", user.getCity()));
                tvEmail.setText(String.format("Email: %s", user.getEmail()));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(ProfileActivity.this, new Bundle());
                Toast.makeText(ProfileActivity.this, "An error occurred, try again.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Token.getInstance().getToken());
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void logout() {
        String url = Constants.URL + "logout";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
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
