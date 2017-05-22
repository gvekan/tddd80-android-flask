package com.example.simsu451.androidprojekt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.chat.ChatActivity;
import com.example.simsu451.androidprojekt.friend.FriendsActivity;
import com.example.simsu451.androidprojekt.friend.RequestsActivity;
import com.example.simsu451.androidprojekt.user.ProfileActivity;
import com.example.simsu451.androidprojekt.wall.WallActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Class intentClass;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.savedInstanceState = savedInstanceState;


        String toast = savedInstanceState.getString("toast");
        if (toast != null && !toast.isEmpty()) Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

        intentClass = WallActivity.class;
        String classString = savedInstanceState.getString("class");
        if (classString != null) {
            String[] className = classString.split(".");
            switch (className[className.length-1]) {
                case "ChatActivity":
                    intentClass = ChatActivity.class;
                    break;
                case "FriendsActivity":
                    intentClass = FriendsActivity.class;
                    break;
                case "RequestsActivity":
                    intentClass = RequestsActivity.class;
                    break;
                case "ProfileActivity":
                    intentClass = ProfileActivity.class;
                    break;
            }
        }

        Button registerButton = (Button) findViewById(R.id.registerButton);
        if (registerButton == null) throw new AssertionError("registerButton is null");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        Button loginButton = (Button) findViewById(R.id.loginButton);
        if (loginButton == null) throw new AssertionError("loginButton is null");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // stop login from going back to an activity when back is pressed
    }

    private void login() {
        String url = Constants.URL + "login";
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        if (etEmail == null) throw new AssertionError("etEmail is null");
        if (etPassword == null) throw new AssertionError("etPassword is null");

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.length() == 0 || password.length() == 0 || password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, "Email or password length is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        final JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse;
                String token = null;
                try {
                    jsonResponse = new JSONObject(response);
                    token = jsonResponse.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Token.getInstance().setToken(token);

                Intent intent = new Intent(LoginActivity.this, intentClass);
                startActivity(intent.putExtras(savedInstanceState));

        }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_LONG).show();
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
        };

        requestQueue.add(stringRequest);
    }

    public static void tokenExpired(Context context, Bundle bundle) {
        bundle.putString("toast", "Please log in again");
        bundle.putString("class", context.getClass().toString());
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent.putExtras(bundle));
        ((Activity) context).finish();
    }

}
