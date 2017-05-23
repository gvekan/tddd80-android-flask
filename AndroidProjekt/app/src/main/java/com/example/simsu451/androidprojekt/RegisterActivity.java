package com.example.simsu451.androidprojekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The RegisterActivity is the activity where you can register a new friend.
 */

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button createButton = (Button) findViewById(R.id.createButton);
        if (createButton == null) throw new AssertionError("createButton is null");
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }


    private void createUser() {
        String url = Constants.URL + "register";

        EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
        EditText etLastName = (EditText) findViewById(R.id.etLastName);
        EditText etCity = (EditText) findViewById(R.id.etCity);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        EditText etControlPassword = (EditText) findViewById(R.id.etControlPassword);

        if (etFirstName == null) throw new AssertionError("etFirstName is null");
        if (etLastName == null) throw new AssertionError("etLastName is null");
        if (etCity == null) throw new AssertionError("etCity is null");
        if (etEmail == null) throw new AssertionError("etEmail is null");
        if (etPassword == null) throw new AssertionError("etPassword is null");
        if (etControlPassword == null) throw new AssertionError("etControlPassword is null");

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String city = etCity.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String controlPassword = etControlPassword.getText().toString();

        if (firstName.length() == 0 || lastName.length() == 0 || city.length() == 0 || email.length() == 0 || password.length() == 0 || controlPassword.length() == 0) {
            Toast.makeText(this, "All fields must be filled in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Not valid email", Toast.LENGTH_SHORT).show();
            etEmail.setText("");
            return;
        }

        if (!password.equals(controlPassword)) {
            Toast.makeText(this, "Not matching passwords", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etControlPassword.setText("");
            return;
        }
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, "Password length too short", Toast.LENGTH_SHORT).show();
            return;
        }
        final JSONObject params = new JSONObject();
        try {
            params.put("first_name", firstName);
            params.put("last_name", lastName);
            params.put("city", city);
            params.put("email", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Email already taken", Toast.LENGTH_SHORT).show();
                etEmail.setText("");
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
}
