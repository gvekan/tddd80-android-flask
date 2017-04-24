package com.example.simsu451.androidprojekt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

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
        String firstname = findViewById(R.id.editFirstname).toString();
        String lastname = findViewById(R.id.editLastname).toString();
        String birthdate = findViewById(R.id.editBirthdate).toString();
        String city = findViewById(R.id.editCity).toString();
        String password = findViewById(R.id.editPassword).toString();
        String controlPassword = findViewById(R.id.controlPassword).toString();

        if (password != controlPassword) { //Kolla så email inte redan finns, födelsedatum är giltigt, bara bokstäver i namnen, lösenorden är bra
            Toast.makeText(this, "Not matching passwords", Toast.LENGTH_LONG).show();
            EditText pw = (EditText) findViewById(R.id.editPassword);
            pw.setText("");
            pw = (EditText) findViewById(R.id.controlPassword);
            pw.setText("");
            return;
        }
             /*
        JSONObject obj = new JSONObject();
        obj.put(firstname
        url/createUser/
        */

    }
}
