package com.example.simsu451.androidprojekt.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    public ChatAdapter(Context context, User user) {
        super(context, R.layout.chat_message);
        messages.setMessages(new ArrayList<Message>());
        this.user = user;
        updateMessages();

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
                tvMessage.setTextColor(5);
            }
            tvMessage.setText(message.getText());
        }
        return convertView;
    }


    public void updateMessages() {
        String url = Constants.URL + "get-latest-messages/" + user.getEmail();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        clear();
                        messages = gson.fromJson(response, Messages.class);
                        addAll(messages.getMessages());
                        notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
