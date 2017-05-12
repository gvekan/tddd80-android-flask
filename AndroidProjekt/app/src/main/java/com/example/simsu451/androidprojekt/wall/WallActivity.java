package com.example.simsu451.androidprojekt.wall;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.ProfileActivity;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WallActivity extends AppCompatActivity {
    private WallAdapter wallAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        Button profileButton = (Button) findViewById(R.id.profileButton);
        if (profileButton == null) throw new AssertionError("profileButton is null");
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button postButton = (Button) findViewById(R.id.postButton);
        if (postButton == null) throw new AssertionError("postButton is null");
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePost();
            }
        });

        ListView listView = (ListView) findViewById(R.id.lwWall);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        if (listView == null) throw new AssertionError("listView is null");
        if (swipeRefreshLayout == null) throw new AssertionError("swipeRefreshLayout is null");
        WallAdapter wallAdapter = new WallAdapter(this, listView, swipeRefreshLayout);
        this.wallAdapter = wallAdapter;
        listView.setAdapter(wallAdapter);

    }


    private void makePost() {
        String url = Constants.URL + "create-post";
        EditText editPost = (EditText) findViewById(R.id.etPost);
        if (editPost == null) throw new AssertionError("editPost is null");
        String text = editPost.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(this, "You have to write something", Toast.LENGTH_SHORT).show();
            return;
        }

        final JSONObject params = new JSONObject();
        try {
            params.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                wallAdapter.updatePostsForUser();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WallActivity.this, "An error occurred, try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

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
