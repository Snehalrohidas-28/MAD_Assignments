package com.example.loginprofileintent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);

        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.equals("Snehal") && password.equals("Snehal@123")) {

                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                openApp("com.example.profile");

            } else {
                Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }
        });


        btnBack.setOnClickListener(v -> goBackToMainApp());


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBackToMainApp();
            }
        });
    }

    
    private void openApp(String packageName) {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Profile App not installed", Toast.LENGTH_LONG).show();
        }
    }

    private void goBackToMainApp() {

        Intent intent = getPackageManager()
                .getLaunchIntentForPackage("com.example.grid");

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Main App not found", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
