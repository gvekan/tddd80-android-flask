package com.example.simsu451.androidprojekt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
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

import java.util.MissingResourceException;

public class LoginActivity extends Activity {
    private Class intentClass;
    private Bundle savedInstanceState;
    private static final String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int LOCATION_REQUEST = 780;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intentClass = WallActivity.class;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //Får appen att krascha om man inte har godkänt innan
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }

        try {
            String toast = savedInstanceState.getString("toast");
            if (toast != null && !toast.isEmpty())
                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
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
            this.savedInstanceState = savedInstanceState;
        } catch (MissingResourceException | NullPointerException ignore) {
            this.savedInstanceState = new Bundle();
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

        final ProgressDialog progress = new ProgressDialog(this);

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

                progress.dismiss();
                Intent intent = new Intent(LoginActivity.this, intentClass);
                startActivity(intent.putExtras(savedInstanceState));

        }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
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

        progress.show(this, "Logging in..", "", true, true);

        requestQueue.add(stringRequest);
    }

    public static void tokenExpired(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_login, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(view);
        Button loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Constants.URL + "login";
                final EditText etEmail = (EditText) view.findViewById(R.id.etEmail);
                final EditText etPassword = (EditText) view.findViewById(R.id.etPassword);

                if (etEmail == null) throw new AssertionError("etEmail is null");
                if (etPassword == null) throw new AssertionError("etPassword is null");

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (email.length() == 0 || password.length() == 0 || password.length() < Constants.MIN_PASSWORD_LENGTH) {
                    Toast.makeText(context, "Email or password length is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog progress = new ProgressDialog(context);

                final JSONObject params = new JSONObject();
                try {
                    params.put("email", email);
                    params.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestQueue requestQueue = Volley.newRequestQueue(context);
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

                        alertDialog.dismiss();
                        progress.dismiss();
                    }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Wrong email or password", Toast.LENGTH_LONG).show();
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

                progress.show(context, "Logging in..", "", true, true);

                requestQueue.add(stringRequest);

            }
        });
        alertDialog.show();
    }

}
