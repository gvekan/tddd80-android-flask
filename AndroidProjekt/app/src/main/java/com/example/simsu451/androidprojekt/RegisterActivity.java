package com.example.simsu451.androidprojekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


public class RegisterActivity extends AppCompatActivity {

    String firstname;
    String lastname;
    String birthdate;
    String city;
    String email;
    String password;
    String controlPassword;

    EditText etFirstname;
    EditText etLastname;
    EditText etBirthdate;
    EditText etCity;
    EditText etEmail;
    EditText etPassword;
    EditText etControlPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button createButton = (Button) findViewById(R.id.createButton);
        assert createButton != null;
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFieldInput();
                createUser();
            }
        });
    }

    private void getFieldInput() {
        etFirstname = (EditText) findViewById(R.id.etFirstname);
        etLastname = (EditText) findViewById(R.id.etLastname);
        etBirthdate = (EditText) findViewById(R.id.etBirthdate);
        etCity = (EditText) findViewById(R.id.etCity);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etControlPassword = (EditText) findViewById(R.id.etControlPassword);

        if (etFirstname == null) throw new AssertionError("etFirstname is null");
        if (etLastname == null) throw new AssertionError("etLastname is null");
        if (etBirthdate == null) throw new AssertionError("etBirthdate is null");
        if (etCity == null) throw new AssertionError("etCity is null");
        if (etEmail == null) throw new AssertionError("etEmail is null");
        if (etPassword == null) throw new AssertionError("etPassword is null");
        if (etControlPassword == null) throw new AssertionError("etControlPassword is null");

        firstname = etFirstname.getText().toString();
        lastname = etLastname.getText().toString();
        birthdate = etBirthdate.getText().toString();
        city = etCity.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        controlPassword = etControlPassword.getText().toString();

    }

    private void createUser() {
        String url = "... /register";

        if (!password.equals(controlPassword)) { //Kolla så email inte redan finns, födelsedatum är giltigt, bara bokstäver i namnen, lösenorden är bra
            Toast.makeText(this, "Not matching passwords", Toast.LENGTH_SHORT).show();
            etPassword.setText("");
            etControlPassword.setText("");
            return;
        }
        final JSONObject params = new JSONObject();
        try {
            params.put("firstname", firstname);
            params.put("lastname", lastname);
            params.put("birthdate",birthdate);
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
