package com.cdtekk.bluetoothapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("Registered")
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonEnter = findViewById(R.id.buttonEnter);
        final EditText editTextCode = findViewById(R.id.editTextCode);

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextCode.getText().toString().equals("9876")){
                    Toast.makeText(LoginActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                    
                    return;
                }

                editTextCode.setText("");

                startActivity(new Intent(LoginActivity.this, ControlActivity.class));
            }
        });
    }
}
