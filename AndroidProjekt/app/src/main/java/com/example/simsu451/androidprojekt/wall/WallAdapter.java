package com.example.simsu451.androidprojekt.wall;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
 * Nätverksanrop från adaptern
 * Override getView
 * notifyDataSetChanged när vi vill uppdatera listan
 */

public class WallAdapter extends ArrayAdapter<Post> {
    private Posts posts = new Posts();
    private boolean flagLoading;
    private boolean scrollListenerActive;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ColorStateList oldColors;
    private boolean oldColorOnce;
    private boolean startup;
    public WallAdapter(Context context, ListView listView, SwipeRefreshLayout swipeRefreshLayout) {
        super(context, R.layout.post);
        posts.setPosts(new ArrayList<Post>());
        flagLoading = true;
        startup = true;
        oldColorOnce = true;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.listView = listView;
        updateLatestPosts();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.i("WallAdapter", "onScroll called from ListView");
                if(scrollListenerActive && !flagLoading && !WallAdapter.this.isEmpty() && firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
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
                        if (!flagLoading) updateLatestPosts();
                        else WallAdapter.this.swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post, parent, false);
        }
        final Post post = getItem(position);
        if (post != null) {
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
            final TextView tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            TextView tvComments = (TextView) convertView.findViewById(R.id.tvComments);
            final FrameLayout container = (FrameLayout) convertView.findViewById(R.id.commentContainer);
            tvName.setText(post.getName());
            if (oldColorOnce) {
                oldColors = tvLikes.getTextColors();
                oldColorOnce = false;
            }
            //System.out.println(post.getName());
            tvText.setText(post.getText());
            if (post.isLiking()) tvLikes.setTextColor(Color.GREEN);
            else tvLikes.setTextColor(oldColors);
            tvLikes.setText(Integer.toString(post.getLikes()));
            View.OnClickListener likesClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (post.isLiking()) {
                        dislikePost(post.getId());
                        post.setLiking(false);
                        post.setLikes(post.getLikes()-1);
                        tvLikes.setText(Integer.toString(post.getLikes()));
                        tvLikes.setTextColor(oldColors);
                    } else {
                        likePost(post.getId());
                        post.setLiking(true);
                        post.setLikes(post.getLikes()+1);
                        tvLikes.setText(Integer.toString(post.getLikes()));
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
//                    LayoutInflater inflater = (LayoutInflater) WallAdapter.this.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//                    View view = inflater.inflate(R.layout.comment_list, container, false);

                    ListView listView = (ListView) finalConvertView.findViewById(R.id.lwComments);
                    if (listView == null) throw new AssertionError("listView is null");
                    listView.setVisibility(View.VISIBLE);
                    final CommentAdapter commentAdapter = new CommentAdapter(WallAdapter.this.getContext(), listView, post.getId(), WallAdapter.this);
                    listView.setAdapter(commentAdapter);
                    final EditText etComment = (EditText) finalConvertView.findViewById(R.id.etComment);
                    etComment.setVisibility(View.VISIBLE);

                    Button commentButton = (Button) finalConvertView.findViewById(R.id.btComment);
                    if (commentButton == null) throw new AssertionError("commentButton is null");
                    commentButton.setVisibility(View.VISIBLE);
                    commentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = Constants.URL + "create-comment/" + post.getId();
                            if (etComment == null) throw new AssertionError("etComment is null");
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
                                    commentAdapter.updateCommentsForUser();
                                    etComment.setText("");
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(WallAdapter.this.getContext(), "An error occurred, try again.", Toast.LENGTH_SHORT).show();
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
                    });

                    Button loadButton = (Button) finalConvertView.findViewById(R.id.btLoad);
                    loadButton.setVisibility(View.VISIBLE);
                    loadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            commentAdapter.updateLatestcommentsFromOldest();
                        }
                    });
                    WallAdapter.this.notifyDataSetChanged();
                }
            };
            tvComments.setText(Integer.toString(post.getComments()));
            tvComments.setOnClickListener(commentsClickListener);
            TextView textComments = (TextView) convertView.findViewById(R.id.textComments);
            textComments.setOnClickListener(commentsClickListener);
        }
        this.notifyDataSetChanged();
        return convertView;
    }
    public void updatePostsForUser() {
        flagLoading = true;
        String url = Constants.URL + "get-latest-posts-from-user";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        posts = gson.fromJson(response, Posts.class);
                        WallAdapter.this.clear();
                        ArrayList<Post> postList = posts.getPosts();
                        Collections.sort(postList, new PostComparator());
                        WallAdapter.this.addAll(postList);
                        scrollListenerActive = true;
                        WallAdapter.this.notifyDataSetChanged();
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
    private void updateLatestPosts() {
        System.out.println(Token.getInstance().getToken());
        flagLoading = true;
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
                            for (int i = size-1; i >= 0; i--) {
                                Post post = postList.get(i);
                                WallAdapter.this.insert(post, 0);
                            }
                            WallAdapter.this.posts.addPosts(postList);
                        }
                        WallAdapter.this.notifyDataSetChanged();
                        if (startup) {
                            startup = false;
                            scrollListenerActive = true;
                        }
                        else if (!postList.isEmpty()) retainPosition(listView.getFirstVisiblePosition() + size);
                        swipeRefreshLayout.setRefreshing(false);
                        flagLoading = false;

                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
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
    private void updateLatestPostsFromOldest() {
        flagLoading = true;
        String url = Constants.URL + "get-latest-posts-from/" + posts.getOldest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (flagLoading) {
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
        View v = listView.getChildAt(listView.getHeaderViewsCount());
        int top = (v == null) ? 0 : v.getTop();
        listView.setSelectionFromTop(position, top);
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