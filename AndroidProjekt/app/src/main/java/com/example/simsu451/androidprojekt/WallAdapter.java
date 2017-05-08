package com.example.simsu451.androidprojekt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Nätverksanrop från adaptern
 * Override getView
 * notifyDataSetChanged när vi vill uppdatera listan
 */

public class WallAdapter extends ArrayAdapter<Post> {

    public WallAdapter(Context context) {
        super(context, R.layout.wall_post);
        updateList();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wall_post, parent, false);
        }
        Post post = getItem(position);
        if (post != null) {
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
            TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            TextView tvDislikes = (TextView) convertView.findViewById(R.id.tvDislikes);

            tvName.setText(post.getName());
            tvText.setText(post.getText());
            tvLikes.setText(post.getLikes());
            tvDislikes.setText(post.getDislikes());
        }
        return convertView;
    }

    public void updateList() {
        String url = ".../posts";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Posts posts = gson.fromJson(response, Posts.class);
                        WallAdapter.this.addAll(posts.getPosts());
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        requestQueue.add(stringRequest);
    }


}
