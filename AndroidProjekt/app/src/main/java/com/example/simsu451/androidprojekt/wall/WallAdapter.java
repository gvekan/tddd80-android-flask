package com.example.simsu451.androidprojekt.wall;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The WallAdapter handles the list of posts, their comments and likes. It uses a SwipeRefreshLayout
 * to allow the user to refresh the page when scrolling
 */

class WallAdapter extends ArrayAdapter<Post> {
    private Posts posts;
    private boolean postsLoading;
    private boolean scrollListenerActive;
    private SwipeRefreshLayout srlPosts;
    private ListView lwPosts;
    private ColorStateList oldColors;
    private boolean oldColorOnce;
    private boolean startup;

    private Comments comments;
    private boolean commentsLoading;
    private Post postWithComments;
    private LinearLayout llComments;
    private Button btHide;
    private Button btLoad;
    private Button btComment;
    private EditText etComment;

    WallAdapter(Context context, ListView listView, SwipeRefreshLayout swipeRefreshLayout) {
        super(context, R.layout.post);
        posts = new Posts();
        posts.setPosts(new ArrayList<Post>());
        comments = new Comments();
        comments.setComments(new ArrayList<Comment>());
        postsLoading = true;
        startup = true;
        oldColorOnce = true;
        this.srlPosts = swipeRefreshLayout;
        this.lwPosts = listView;
        updateLatestPosts();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.i("WallAdapter", "onScroll called from ListView");
                if(scrollListenerActive && !postsLoading && !WallAdapter.this.isEmpty() && firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
                {
                    updateLatestPostsFromOldest();
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
                        if (!postsLoading) updateLatestPosts();
                        else WallAdapter.this.srlPosts.setRefreshing(false);
                        hideComments();
                    }
                }
        );
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post, parent, false);
        }
        final Post post = getItem(position);
        if (post != null) {

            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
            TextView tvCity = (TextView) convertView.findViewById(R.id.tvCity);
            final TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            final TextView tvComments = (TextView) convertView.findViewById(R.id.tvComments);

            if (oldColorOnce) {
                oldColors = tvLikes.getTextColors();
                oldColorOnce = false;
            }

            if (post.isCommentsShowing()) {
                View visible = convertView.findViewById(R.id.llComments);
                visible.setVisibility(View.VISIBLE);
                visible = convertView.findViewById(R.id.etComment);
                visible.setVisibility(View.VISIBLE);
                visible = convertView.findViewById(R.id.btComment);
                visible.setVisibility(View.VISIBLE);
                visible = convertView.findViewById(R.id.btLoad);
                visible.setVisibility(View.VISIBLE);
                visible = convertView.findViewById(R.id.btHide);
                visible.setVisibility(View.VISIBLE);
            } else {
                View gone = convertView.findViewById(R.id.llComments);
                gone.setVisibility(View.GONE);
                gone = convertView.findViewById(R.id.etComment);
                gone.setVisibility(View.GONE);
                gone = convertView.findViewById(R.id.btComment);
                gone.setVisibility(View.GONE);
                gone = convertView.findViewById(R.id.btLoad);
                gone.setVisibility(View.GONE);
                gone = convertView.findViewById(R.id.btHide);
                gone.setVisibility(View.GONE);
            }

            tvName.setText(post.getName());
            tvText.setText(post.getText());
            tvCity.setText(post.getCity());
            tvLikes.setText(String.format("%s", post.getLikes()));
            tvComments.setText(String.format("%s", post.getComments()));

            if (post.isLiking()) tvLikes.setTextColor(Color.GREEN);
            else tvLikes.setTextColor(oldColors);

            View.OnClickListener likesClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.isLiking()) {
                        dislikePost(post.getId());
                        post.setLiking(false);
                        post.setLikes(post.getLikes()-1);
                        tvLikes.setText(String.format("%s", post.getLikes()));
                        tvLikes.setTextColor(oldColors);
                    } else {
                        likePost(post.getId());
                        post.setLiking(true);
                        post.setLikes(post.getLikes()+1);
                        tvLikes.setText(String.format("%s", post.getLikes()));
                        tvLikes.setTextColor(Color.GREEN);
                    } WallAdapter.this.notifyDataSetChanged();
                }
            };

            tvLikes.setOnClickListener(likesClickListener);
            TextView textLikes = (TextView) convertView.findViewById(R.id.textLikes);
            textLikes.setOnClickListener(likesClickListener);

            final View finalConvertView = convertView;
            View.OnClickListener commentsClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideComments();

                    llComments = (LinearLayout) finalConvertView.findViewById(R.id.llComments);
                    etComment = (EditText) finalConvertView.findViewById(R.id.etComment);
                    btComment = (Button) finalConvertView.findViewById(R.id.btComment);
                    btLoad = (Button) finalConvertView.findViewById(R.id.btLoad);
                    btHide = (Button) finalConvertView.findViewById(R.id.btHide);
                    postWithComments = post;

                    postWithComments.setCommentsShowing(true);
                    llComments.setVisibility(View.VISIBLE);
                    etComment.setVisibility(View.VISIBLE);
                    btComment.setVisibility(View.VISIBLE);
                    btLoad.setVisibility(View.VISIBLE);
                    btHide.setVisibility(View.VISIBLE);

                    updateLatestComments();

                    btHide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideComments();
                        }
                    });

                    btComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = Constants.URL + "create-comment/" + post.getId();
                            String text = etComment.getText().toString();
                            if (text.isEmpty()) {
                                Toast.makeText(WallAdapter.this.getContext(), "You have to write something", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            final JSONObject params = new JSONObject();
                            try {
                                params.put("text", text);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RequestQueue requestQueue = Volley.newRequestQueue(WallAdapter.this.getContext());
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    updateCommentsForUser();
                                    post.setComments(post.getComments() + 1);
                                    tvComments.setText(String.format("%s", post.getComments()));
                                    etComment.setText("");
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(getContext(), new Bundle());
                                    Toast.makeText(WallAdapter.this.getContext(), "An error occurred, try again.", Toast.LENGTH_SHORT).show();
                                }
                            }) {
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
                    });

                    btLoad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateLatestCommentsFromOldest();
                        }
                    });
                    notifyDataSetChanged();
                }
            };

            tvComments.setOnClickListener(commentsClickListener);
            TextView textComments = (TextView) convertView.findViewById(R.id.textComments);
            textComments.setOnClickListener(commentsClickListener);
        }

        this.notifyDataSetChanged();
        return convertView;
    }

    void updatePostsForUser() {
        postsLoading = true;
        String url = Constants.URL + "get-latest-posts-from-user";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Posts posts = gson.fromJson(response, Posts.class);
                        ArrayList<Post> postList = posts.getPosts();
                        Collections.sort(postList, new PostComparator());
                        WallAdapter.this.posts = new Posts();
                        WallAdapter.this.posts.setPosts(postList);
                        WallAdapter.this.clear();
                        WallAdapter.this.addAll(postList);
                        scrollListenerActive = true;
                        WallAdapter.this.notifyDataSetChanged();
                        postsLoading = false;
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        postsLoading = false;
                        if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(getContext(), new Bundle());
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

    private void updateLatestPosts() {
        postsLoading = true;
        String url = Constants.URL + "get-latest-posts/" + posts.getLatest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Posts posts = gson.fromJson(response, Posts.class);
                        ArrayList<Post> postList = posts.getPosts();
                        Collections.sort(postList, new PostComparator());
                        int size = postList.size();
                        if (!postList.isEmpty()) {
                            for (int i = size - 1; i >= 0; i--) {
                                Post post = postList.get(i);
                                WallAdapter.this.insert(post, 0);
                            }
                            WallAdapter.this.posts.addPosts(postList);
                        }
                        WallAdapter.this.notifyDataSetChanged();
                        if (startup) {
                            startup = false;
                            scrollListenerActive = true;
                        } else if (!postList.isEmpty())
                            retainPosition(lwPosts.getFirstVisiblePosition() + size);
                        srlPosts.setRefreshing(false);
                        postsLoading = false;

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        srlPosts.setRefreshing(false);
                        postsLoading = false;
                        if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(getContext(), new Bundle());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Token.getInstance().getToken());
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateLatestPostsFromOldest() {
        postsLoading = true;
        String url = Constants.URL + "get-latest-posts-from/" + posts.getOldest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (postsLoading) {
                            Gson gson = new Gson();
                            Posts posts = gson.fromJson(response, Posts.class);
                            ArrayList<Post> postList = posts.getPosts();
                            Collections.sort(postList, new PostComparator());
                            WallAdapter.this.posts.addPosts(postList);
                            WallAdapter.this.addAll(postList);
                            WallAdapter.this.notifyDataSetChanged();
                            if (WallAdapter.this.posts.getOldest() == 1) {
                                scrollListenerActive = false;
                            }
                            retainPosition(lwPosts.getFirstVisiblePosition());
                            postsLoading = false;
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (postsLoading) {
                            postsLoading = false;
                        }
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

    /**
     * keeps the position it had before data was added
     */
    private void retainPosition(final int position) {
        View v = lwPosts.getChildAt(lwPosts.getHeaderViewsCount());
        int top = (v == null) ? 0 : v.getTop();
        lwPosts.setSelectionFromTop(position, top);
    }

    private void likePost(int post) {
        String url = Constants.URL + "like-post/" + post;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

    private void dislikePost(int post) {
        String url = Constants.URL + "dislike-post/" + post;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

    private void updateCommentsForUser() {
        commentsLoading = true;
        String url = Constants.URL + "get-latest-comments-from-user/" + postWithComments.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Comments comments = gson.fromJson(response, Comments.class);
                        ArrayList<Comment> commentList = comments.getComments();
                        Collections.sort(commentList, new CommentComparator());
                        WallAdapter.this.comments = new Comments();
                        WallAdapter.this.comments.setComments(commentList);
                        WallAdapter.this.replaceAllComments();
                        WallAdapter.this.notifyDataSetChanged();
                        commentsLoading = false;
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        commentsLoading = false;
                        if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(getContext(), new Bundle());
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
        commentsLoading = true;
        String url = Constants.URL + "get-latest-comments/" + postWithComments.getId() + "/" + comments.getLatest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Comments comments = gson.fromJson(response, Comments.class);
                        WallAdapter.this.comments.addComments(comments.getComments());
                        WallAdapter.this.replaceAllComments();
                        WallAdapter.this.notifyDataSetChanged();
                        commentsLoading = false;

                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        commentsLoading = false;
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

    private void updateLatestCommentsFromOldest() {
        commentsLoading = true;
        String url = Constants.URL + "get-latest-comments-from/" + postWithComments.getId() + "/" + comments.getOldest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Comments comments = gson.fromJson(response, Comments.class);
                        WallAdapter.this.comments.addComments(comments.getComments());
                        WallAdapter.this.replaceAllComments();
                        WallAdapter.this.notifyDataSetChanged();
                        commentsLoading = false;
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        commentsLoading = false;
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

    private void replaceAllComments() {
        llComments.removeAllViews();
        ArrayList<Comment> commentList = comments.getComments();
        for (Comment comment:
                commentList) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate(R.layout.comment, llComments, false);
            TextView tvName = (TextView) view.findViewById(R.id.tvCommentName);
            TextView tvText = (TextView) view.findViewById(R.id.tvCommentText);
            tvName.setText(comment.getName());
            tvText.setText(comment.getText());
            llComments.addView(view);
            Log.i("replaceAllComments", "Adding comment");
        }
    }

    private void hideComments() {
        comments = new Comments();
        comments.setComments(new ArrayList<Comment>());
        if (postWithComments != null) {
            postWithComments.setCommentsShowing(false);
            llComments.removeAllViews();
            llComments.setVisibility(View.GONE);
            etComment.setVisibility(View.GONE);
            btHide.setVisibility(View.GONE);
            btLoad.setVisibility(View.GONE);
            btComment.setVisibility(View.GONE);
            notifyDataSetChanged();
        }
    }
}