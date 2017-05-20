package com.example.simsu451.androidprojekt.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by simsu451 on 20/05/17.
 */

public class RequestsAdapter extends ArrayAdapter<Friend> {

    public RequestsAdapter(Context context) {
        super(context, R.layout.activity_friends);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend, parent, false);
        }
        final Friend friend = getItem(position);
        if (friend != null) {
            Button addButton = (Button) convertView.findViewById(R.id.button);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriend(friend);
                }
            });

        }
        return convertView;
    }


    private void addFriend(Friend friend) {
        String url = Constants.URL + "accept-friend-request/" + friend.getEmail();
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
