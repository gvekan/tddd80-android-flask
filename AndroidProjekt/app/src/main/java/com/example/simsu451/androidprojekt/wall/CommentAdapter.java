package com.example.simsu451.androidprojekt.wall;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Nätverksanrop från adaptern
 * Override getView
 * notifyDataSetChanged när vi vill uppdatera listan
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Comments comments = new Comments();
    private boolean flagLoading;
    private ListView listView;
    private boolean startup;
    private int postId;
    private WallAdapter wallAdapter;
    public CommentAdapter(Context context, ListView listView, int postId, WallAdapter wallAdapter) {
        super(context, R.layout.comment);
        this.postId = postId;
        this.wallAdapter = wallAdapter;
        comments.setComments(new ArrayList<Comment>());
        flagLoading = true;
        startup = true;
        this.listView = listView;
        updateLatestComments();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment, parent, false);
        }
        final Comment comment = getItem(position);
        if (comment != null) {
            TextView tvName = (TextView) convertView.findViewById(R.id.tvCommentName);
            TextView tvText = (TextView) convertView.findViewById(R.id.tvCommentText);
            tvName.setText(comment.getName());
            tvText.setText(comment.getText());
        }
        this.notifyDataSetChanged();
        wallAdapter.notifyDataSetChanged();
        return convertView;
    }
    public void updateCommentsForUser() {
        flagLoading = true;
        String url = Constants.URL + "get-latest-comments-from-user/" + postId;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        comments = gson.fromJson(response, Comments.class);
                        CommentAdapter.this.clear();
                        ArrayList<Comment> commentList = comments.getComments();
                        Collections.sort(commentList, new CommentComparator());
                        CommentAdapter.this.addAll(commentList);
                        CommentAdapter.this.notifyDataSetChanged();
                        wallAdapter.notifyDataSetChanged();
                        flagLoading = false;
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        flagLoading = false;
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Token.getInstance().getToken());
                return headers;
            }};
        requestQueue.add(stringRequest);
    }
    private void updateLatestComments() {
        System.out.println(Token.getInstance().getToken());
        flagLoading = true;
        String url = Constants.URL + "get-latest-comments/" + postId + "/" + comments.getLatest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Comments comments = gson.fromJson(response, Comments.class);
                        ArrayList<Comment> commentList = comments.getComments();
                        Collections.sort(commentList, new CommentComparator());
                        int size = commentList.size();
                        if (!commentList.isEmpty()) {
                            for (int i = size-1; i >= 0; i--) {
                                Comment comment = commentList.get(i);
                                CommentAdapter.this.insert(comment, 0);
                            }
                            CommentAdapter.this.comments.addComments(commentList);
                        }
                        CommentAdapter.this.notifyDataSetChanged();
                        wallAdapter.notifyDataSetChanged();
                        if (startup) {
                            startup = false;
                        }
                        else if (!commentList.isEmpty()) retainPosition(listView.getFirstVisiblePosition() + size);
                        flagLoading = false;

                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        flagLoading = false;
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
    public void updateLatestcommentsFromOldest() {
        flagLoading = true;
        String url = Constants.URL + "get-latest-comments-from/" + postId + "/" + comments.getOldest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (flagLoading) {
                            Gson gson = new Gson();
                            Comments comments = gson.fromJson(response, Comments.class);
                            ArrayList<Comment> commentList = comments.getComments();
                            Collections.sort(commentList, new CommentComparator());
                            CommentAdapter.this.comments.addComments(commentList);
                            CommentAdapter.this.addAll(commentList);
                            CommentAdapter.this.notifyDataSetChanged();
                            wallAdapter.notifyDataSetChanged();
                            retainPosition(listView.getFirstVisiblePosition());
                            flagLoading = false;
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (flagLoading) {
                            flagLoading = false;
                        }
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
    /**
     * keeps the position it had before data was added
     */
    private void retainPosition(final int position) {
//        View v = listView.getChildAt(listView.getHeaderViewsCount());
//        int top = (v == null) ? 0 : v.getTop();
//        listView.setSelectionFromTop(position, top);
    }
}