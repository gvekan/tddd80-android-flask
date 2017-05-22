package com.example.simsu451.androidprojekt.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.simsu451.androidprojekt.wall.WallAdapter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter chatAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        String jsonFriend = extras.getString("user"); // http://stackoverflow.com/questions/4249897/how-to-send-objects-through-bundle
        user = new Gson().fromJson(jsonFriend, User.class);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        if (tvName == null) throw new AssertionError("tvName is null");
        tvName.setText(user.getFirstName() + ' ' + user.getLastName());

        ListView lwChat = (ListView) findViewById(R.id.lwChat);
        if (lwChat == null) throw new AssertionError("listView is null");
        chatAdapter = new ChatAdapter(this, user, lwChat);
        lwChat.setAdapter(chatAdapter);
        lwChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.i("WallAdapter", "onScroll called from ListView");
                if(!chatAdapter.isEmpty() && firstVisibleItem == 0 && totalItemCount!=0)
                {
                    chatAdapter.updateLatestMessagesFromOldest();

                }
            }
        });

        Button sendButton = (Button) findViewById(R.id.sendButton);
        if (sendButton == null) throw new AssertionError("sendButton is null");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }


    private void sendMessage() {
        String url = Constants.URL + "send-message";
        EditText etMessage = (EditText) findViewById(R.id.etMessage);
        if (etMessage == null) throw new AssertionError("etMessage is null");
        String text = etMessage.getText().toString();
        if (text.isEmpty()) { //Do nothing if no text
            return;
        }
        final JSONObject params = new JSONObject();
        try {
            params.put("text", text);
            params.put("receiver", user.getEmail());
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                chatAdapter.updateLatestMessages();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return params.toString().getBytes();
            }
            @Override
            public String getBodyContentType() { return "application/json";}

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
