package com.example.simsu451.androidprojekt;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

/**
 * Nätverksanrop från adaptern
 * Override getView
 * notifyDataSetChanged när vi vill uppdatera listan
 */

public class WallAdapter extends ArrayAdapter {
    public WallAdapter(Context context, int resource) {
        super(context, resource);
        String url = ".../posts";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Posts posts = gson.fromJson(response, Posts.class);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        requestQueue.add(stringRequest);
    }


}
