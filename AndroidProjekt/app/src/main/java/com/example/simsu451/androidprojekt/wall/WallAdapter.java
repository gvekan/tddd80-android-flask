package com.example.simsu451.androidprojekt.wall;

import android.content.Context;
import android.graphics.Color;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Nätverksanrop från adaptern
 * Override getView
 * notifyDataSetChanged när vi vill uppdatera listan
 */

public class WallAdapter extends ArrayAdapter<Post> {
    private Posts posts = new Posts();
    private boolean flag_loading;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private TextView tvLikes;

    public WallAdapter(Context context, ListView listView, SwipeRefreshLayout swipeRefreshLayout) {
        super(context, R.layout.wall_post);
        posts.setPosts(new ArrayList<Post>());
        flag_loading = true;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.listView = listView;
        updateLatestPosts();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.i("WallAdapter", "onScroll called from ListView");

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    if(!flag_loading && !WallAdapter.this.isEmpty()) updateLatestPostsFromOldest();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("WallAdapter", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        if (!flag_loading) updateLatestPosts();
                        else WallAdapter.this.swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wall_post, parent, false);
        }
        final Post post = getItem(position);
        if (post != null) {
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
            final TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            TextView tvComments = (TextView) convertView.findViewById(R.id.tvComments);

            tvName.setText(post.getName());
            tvText.setText(post.getText());
            if (post.isLiking()) tvLikes.setTextColor(Color.GREEN);
            tvLikes.setText(post.getLikes());

            View.OnClickListener likesClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.isLiking()) {
                        dislikePost(post.getId());
                        post.setLiking(false);
                        post.setLikes(post.getLikes()-1);
                        tvLikes.setTextColor(Color.BLACK);
                    } else {
                        likePost(post.getId());
                        post.setLiking(true);
                        post.setLikes(post.getLikes()+1);
                        tvLikes.setTextColor(Color.GREEN);
                    }
                }
            };
            tvLikes.setOnClickListener(likesClickListener);
            TextView textLikes = (TextView) convertView.findViewById(R.id.textLikes);
            textLikes.setOnClickListener(likesClickListener);

            View.OnClickListener commentsClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            };
            tvComments.setText(post.getComments());
            tvComments.setOnClickListener(commentsClickListener);
            TextView textComments = (TextView) convertView.findViewById(R.id.textComments);
            textComments.setOnClickListener(likesClickListener);

        }
        return convertView;
    }

    public void updatePostsForUser() {
        flag_loading = true;
        String url = Constants.URL + "get-latest-posts-from-user";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Posts posts = gson.fromJson(response, Posts.class);
                        WallAdapter.this.clear();
                        WallAdapter.this.addAll(posts.getPosts());
                        WallAdapter.this.posts = posts;
                        WallAdapter.this.notifyDataSetChanged();
                        flag_loading = false;
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        flag_loading = false;
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private void updateLatestPosts() {
        flag_loading = true;
        String url = Constants.URL + "get-latest-posts";
        final JSONObject params = new JSONObject();
        try {
            params.put("post", posts.getLatest());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Posts posts = gson.fromJson(response, Posts.class);
                        List<Post> postList = posts.getPosts();
                        int size = postList.size();
                        if (!postList.isEmpty()) {
                            for (int i = size-1; i >= 0; i--) {
                                Post post = postList.get(i);
                                WallAdapter.this.insert(post, 0);
                            }
                            WallAdapter.this.posts.addPosts(postList);
                        }
                        WallAdapter.this.notifyDataSetChanged();
                        if (!postList.isEmpty() && !WallAdapter.this.isEmpty()) retainPosition(listView.getFirstVisiblePosition() + size);
                        swipeRefreshLayout.setRefreshing(false);
                        flag_loading = false;
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        flag_loading = false;
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateLatestPostsFromOldest() {
        flag_loading = true;
        final JSONObject params = new JSONObject();
        try {
            params.put("post", posts.getOldest());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.URL + "get-latest-posts-from";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!flag_loading) {
                            Gson gson = new Gson();
                            Posts posts = gson.fromJson(response, Posts.class);
                            WallAdapter.this.posts.addPosts(posts.getPosts());
                            WallAdapter.this.addAll(posts.getPosts());
                            WallAdapter.this.notifyDataSetChanged();
                            retainPosition(listView.getFirstVisiblePosition());
                            flag_loading = false;
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!flag_loading) {
                            flag_loading = false;
                        }
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * keeps the position it had before data was added
     */
    private void retainPosition(final int position) {
        View v = listView.getChildAt(listView.getHeaderViewsCount());
        int top = (v == null) ? 0 : v.getTop();
        listView.setSelectionFromTop(position, top);
    }

    private void likePost(int post) {
        final JSONObject params = new JSONObject();
        try {
            params.put("post", post);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.URL + "like-post";
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
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(stringRequest);
    }

    private void dislikePost(int post) {
        final JSONObject params = new JSONObject();
        try {
            params.put("post", post);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.URL + "dislike-post";
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
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        requestQueue.add(stringRequest);
    }
}
