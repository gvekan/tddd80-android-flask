package com.example.simsu451.androidprojekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        if (registerButton == null) throw new AssertionError("registerButton is null");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);;
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

    private void login() {
        String url = "../user";
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        if (etEmail == null) throw new AssertionError("etEmail is null");
        if (etPassword == null) throw new AssertionError("etPassword is null");

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        final JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomStringRequest stringRequest = new CustomStringRequest(Request.Method.POST, url, new Response.Listener<CustomStringRequest.ResponseM>() {
            @Override
            public void onResponse(CustomStringRequest.ResponseM result) {
                // HÄR SKA VI OCKSÅ HÄMTA TOKEN
                String token = result.headers.get("Authorization");
                Bundle bundle = new Bundle();
                bundle.putString("token", token);

                Intent intent = new Intent(LoginActivity.this, WallActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_LONG).show();
                etEmail.setText("");
                etPassword.setText("");
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



    public class CustomStringRequest extends Request<CustomStringRequest.ResponseM> {


        private Response.Listener<CustomStringRequest.ResponseM> mListener;

        public CustomStringRequest(int method, String url, Response.Listener<CustomStringRequest.ResponseM> responseListener, Response.ErrorListener listener) {
            super(method, url, listener);
            this.mListener = responseListener;
        }


        @Override
        protected void deliverResponse(ResponseM response) {
            this.mListener.onResponse(response);
        }

        @Override
        protected Response<ResponseM> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }

            ResponseM responseM = new ResponseM();
            responseM.headers = response.headers;
            responseM.response = parsed;

            return Response.success(responseM, HttpHeaderParser.parseCacheHeaders(response));
        }


        public class ResponseM {
            Map<String, String> headers;
            String response;
        }

    }

}
