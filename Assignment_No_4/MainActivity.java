package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    Button b, b2, b3, btnBack;
    ConstraintLayout c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        btnBack = findViewById(R.id.btnBack);
        c = findViewById(R.id.main);


        b.setOnClickListener(v -> c.setBackgroundResource(R.drawable.panda));
        b2.setOnClickListener(v -> c.setBackgroundResource(R.drawable.lion));
        b3.setOnClickListener(v -> c.setBackgroundResource(R.drawable.parrot));


        btnBack.setOnClickListener(v -> goBackToMainApp());


        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        goBackToMainApp();
                    }
                });
    }


    private void goBackToMainApp() {

        Intent intent = getPackageManager()
                .getLaunchIntentForPackage("com.example.grid"); // change if needed

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Main App not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
