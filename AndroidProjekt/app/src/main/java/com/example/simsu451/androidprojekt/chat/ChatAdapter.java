package com.example.simsu451.androidprojekt.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.simsu451.androidprojekt.friend.User;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by simsu451 on 10/05/17.
 */

public class ChatAdapter extends ArrayAdapter<Message> {
    private Messages messages = new Messages();
    private User user;
    private ListView lwChat;
    private SwipeRefreshLayout srlChat;

    public ChatAdapter(Context context, User user, ListView lwChat, final SwipeRefreshLayout srlChat) {
        super(context, R.layout.chat_message);
        this.lwChat = lwChat;
        this.srlChat = srlChat;
        messages.setMessages(new ArrayList<Message>());
        this.user = user;
        updateLatestMessages();
        srlChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLatestMessagesFromOldest();
            }
        });

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message, parent, false);
        }
        Message message = getItem(position);
        if (message != null) {
            TextView tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
            if (message.getSentBy().equals(user.getEmail())) {
                tvMessage.setTextColor(Color.GREEN);
            }
            tvMessage.setText(message.getText());
        }
        return convertView;
    }

    public void updateLatestMessages() {
        String url = Constants.URL + "get-latest-messages/" + user.getEmail() + "/" + messages.getLatest();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        messages.addMessages(gson.fromJson(response, Messages.class).getMessages());
                        clear();
                        addAll(messages.getMessages());
                        notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    ChatActivity chatActivity = (ChatActivity) getContext();
                    chatActivity.tokenExpired();
                }

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

    public void updateLatestMessagesFromOldest() {
        srlChat.setRefreshing(false);
        String url = Constants.URL + "get-latest-messages-from/" + user.getEmail() + "/" + messages.getOldest();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        messages.addMessages(gson.fromJson(response, Messages.class).getMessages());
                        clear();
                        addAll(messages.getMessages());
                        int size = messages.getMessages().size();
                        int position = lwChat.getFirstVisiblePosition() + size;
                        View v = lwChat.getChildAt(lwChat.getHeaderViewsCount());
                        int top = (v == null) ? 0 : v.getTop();
                        lwChat.setSelectionFromTop(position, top);
                        notifyDataSetChanged();
                        srlChat.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                srlChat.setRefreshing(false);
                if (error.networkResponse.statusCode == 401) {
                    ChatActivity chatActivity = (ChatActivity) getContext();
                    chatActivity.tokenExpired();
                }
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

}
