package com.example.simsu451.androidprojekt.wall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.simsu451.androidprojekt.Constants;
import com.example.simsu451.androidprojekt.UsersActivity;
import com.example.simsu451.androidprojekt.LoginActivity;
import com.example.simsu451.androidprojekt.user.ProfileActivity;
import com.example.simsu451.androidprojekt.R;
import com.example.simsu451.androidprojekt.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WallActivity extends AppCompatActivity implements LocationListener {
    private WallAdapter wallAdapter;
    LocationManager lm;
    private static final String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int LOCATION_REQUEST = 780;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);

        Button profileButton = (Button) findViewById(R.id.profileButton);
        if (profileButton == null) throw new AssertionError("profileButton is null");
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button usersButton = (Button) findViewById(R.id.usersButton);
        if (usersButton == null) throw new AssertionError("usersButton is null");
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });
        Button postButton = (Button) findViewById(R.id.postButton);
        if (postButton == null) throw new AssertionError("postButton is null");
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePost();
            }
        });

        ListView listView = (ListView) findViewById(R.id.lwWall);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        if (listView == null) throw new AssertionError("listView is null");
        if (swipeRefreshLayout == null) throw new AssertionError("swipeRefreshLayout is null");
        WallAdapter wallAdapter = new WallAdapter(this, listView, swipeRefreshLayout);
        this.wallAdapter = wallAdapter;
        listView.setAdapter(wallAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    public String getCity(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (addresses == null || addresses.size() == 0) {
            return "";
        }
        return addresses.get(0).getLocality();
    }

    private void makePost() {
        String url = Constants.URL + "create-post";
        final EditText etPost = (EditText) findViewById(R.id.etPost);
        if (etPost == null) throw new AssertionError("etPost is null");
//        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_wall);
//        if (relativeLayout == null) throw new AssertionError("relativeLayout is null");
        String text = etPost.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(this, "You have to write something", Toast.LENGTH_SHORT).show();
            return;
        }
        String city = "";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                city = getCity(location.getLatitude(), location.getLongitude());
            }
        }

        final JSONObject params = new JSONObject();
        try {
            params.put("text", text);
            params.put("city", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                wallAdapter.updatePostsForUser();
                etPost.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 401) LoginActivity.tokenExpired(WallActivity.this, new Bundle());
                Toast.makeText(WallActivity.this, "An error occurred, try again.", Toast.LENGTH_SHORT).show();
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
