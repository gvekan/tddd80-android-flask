package com.example.simsu451.androidprojekt.friend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.simsu451.androidprojekt.chat.ChatActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by simsu451 on 20/05/17.
 */

public class FriendsAdapter extends ArrayAdapter<Friend> {

    public FriendsAdapter(Context context) {
        super(context, R.layout.activity_friends);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend, parent, false);
        }
        final Friend friend = getItem(position);
        if (friend != null) {
            TextView tvFriend = (TextView) convertView.findViewById(R.id.tvFriend);
            Button button = (Button) convertView.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriend(friend);
                }
            });
            tvFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("friend", new Gson().toJson(friend)); // http://stackoverflow.com/questions/4249897/how-to-send-objects-through-bundle
                    getContext().startActivity(intent);
                }
            });
            }
        return convertView;
    }


    private void deleteFriend(Friend friend) {
        String url = Constants.URL + "delete-friend/" + friend.getEmail();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        notifyDataSetChanged();
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
