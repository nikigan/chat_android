package com.example.jijagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);


        Button loginButton = findViewById(R.id.loginButton);
        final EditText username = findViewById(R.id.usernameTextField);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usrName = username.getText().toString();

                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.putExtra("userName", usrName);

                startActivity(intent);
            }
        });
    }




}
