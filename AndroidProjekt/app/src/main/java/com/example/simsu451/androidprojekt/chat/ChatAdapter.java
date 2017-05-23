package com.example.simsu451.androidprojekt.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
 * The ChatAdapter handles the ChatActivity. It handles the list of <Message> in a chat.
 */

class ChatAdapter extends ArrayAdapter<Message> {
    private Messages messages = new Messages();
    private User friend;
    private ListView lwChat;
    private SwipeRefreshLayout srlChat;

    ChatAdapter(Context context, User friend, ListView lwChat, final SwipeRefreshLayout srlChat) {
        super(context, R.layout.message);
        this.lwChat = lwChat;
        this.srlChat = srlChat;
        messages.setMessages(new ArrayList<Message>());
        this.friend = friend;
        updateLatestMessages();
        srlChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLatestMessagesFromOldest();
            }
        });

    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
        }
        Message message = getItem(position);
        if (message != null) {
            FrameLayout flMessage = (FrameLayout) convertView.findViewById(R.id.flMessage);
            View dialogView;
            TextView tvDialog;
            if (message.getSentBy().equals(friend.getEmail())) {
                dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_friend, flMessage, false);
                tvDialog = (TextView) dialogView.findViewById(R.id.tvFriendDialog);
            } else {
                dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user, flMessage, false);
                tvDialog = (TextView) dialogView.findViewById(R.id.tvUserDialog);
                tvDialog.setText(message.getText());
            }
            tvDialog.setText(message.getText());
            flMessage.addView(dialogView);
            notifyDataSetChanged();
        }
        return convertView;
    }

    void updateLatestMessages() {
        String url = Constants.URL + "get-latest-messages/" + friend.getEmail() + "/" + messages.getLatest();

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

    private void updateLatestMessagesFromOldest() {
        srlChat.setRefreshing(false);
        String url = Constants.URL + "get-latest-messages-from/" + friend.getEmail() + "/" + messages.getOldest();
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
