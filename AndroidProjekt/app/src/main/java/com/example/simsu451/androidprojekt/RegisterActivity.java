package com.example.simsu451.androidprojekt;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }



    private void createUser() {
        String url = "... /createuser";
        final String firstname = findViewById(R.id.editFirstname).toString();
        final String lastname = findViewById(R.id.editLastname).toString();
        final String birthdate = findViewById(R.id.editBirthdate).toString();
        final String city = findViewById(R.id.editCity).toString();
        final String password = findViewById(R.id.editPassword).toString();
        String controlPassword = findViewById(R.id.controlPassword).toString();

        if (!password.equals(controlPassword)) { //Kolla så email inte redan finns, födelsedatum är giltigt, bara bokstäver i namnen, lösenorden är bra
            Toast.makeText(this, "Not matching passwords", Toast.LENGTH_LONG).show();
            EditText pw = (EditText) findViewById(R.id.editPassword);
            if (pw != null) {
                pw.setText("");
            }
            pw = (EditText) findViewById(R.id.controlPassword);
            if (pw != null) {
                pw.setText("");
            }
            return;
        }
        final JSONObject params = new JSONObject();
        try {
            params.put("firstname", firstname);
            params.put("lastname", lastname);
            params.put("birthdate",birthdate);
            params.put("city", city);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
