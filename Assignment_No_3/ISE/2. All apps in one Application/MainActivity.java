package com.example.grid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btn10 = findViewById(R.id.btn10);
        btn11 = findViewById(R.id.btn11);
        btn12 = findViewById(R.id.btn12);
        btn13 = findViewById(R.id.btn13);

        btn1.setOnClickListener(v -> openApp("com.example.myapplication"));
        btn2.setOnClickListener(v -> openApp("com.example.linearvertical"));
        btn3.setOnClickListener(v -> openApp("com.example.linearhorizontal"));
        btn4.setOnClickListener(v -> openApp("com.example.tableexample"));
        btn5.setOnClickListener(v -> openApp("com.example.gridexample"));
        btn6.setOnClickListener(v -> openApp("com.example.frameexample"));
        btn7.setOnClickListener(v -> openApp("com.example.relativeexample"));
        btn8.setOnClickListener(v -> openApp("com.example.absoluteexample"));
        btn9.setOnClickListener(v -> openApp("com.example.loginprofileintent"));
        btn10.setOnClickListener(v -> openApp("com.example.calculator"));
        btn11.setOnClickListener(v -> openApp("com.example.listexample"));
        btn12.setOnClickListener(v -> openApp("com.example.intentexamples"));
        btn13.setOnClickListener(v -> openApp("com.example.profile"));
    }

    private void openApp(String packageName) {

        try {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageName);

            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "App not installed:\n" + packageName, Toast.LENGTH_LONG).show();
                Log.e("APP_ERROR", "Not found: " + packageName);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error opening app", Toast.LENGTH_SHORT).show();
            Log.e("APP_ERROR", e.toString());
        }
    }
}
