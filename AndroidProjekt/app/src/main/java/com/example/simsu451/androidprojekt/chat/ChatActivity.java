package com.example.simsu451.androidprojekt.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.Friend;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ListView listView = (ListView) findViewById(R.id.lwChat);
        final Bundle bundle = getIntent().getExtras();


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
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //chatAdapter.updateMessages
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
