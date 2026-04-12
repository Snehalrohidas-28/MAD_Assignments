package com.example.gridexample;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btnBack).setOnClickListener(v -> goBackToMainApp());

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
                .getLaunchIntentForPackage("com.example.grid"); 

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Main App not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
