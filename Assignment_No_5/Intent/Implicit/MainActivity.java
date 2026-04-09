package com.example.implicitexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnWebsite, btnCall, btnSMS, btnEmail, btnCamera, btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWebsite = findViewById(R.id.btnWebsite);
        btnCall = findViewById(R.id.btnCall);
        btnSMS = findViewById(R.id.btnSMS);
        btnEmail = findViewById(R.id.btnEmail);
        btnCamera = findViewById(R.id.btnCamera);
        btnMap = findViewById(R.id.btnMap);

        btnWebsite.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.google.com"));
            startActivity(i);
        });

        btnCall.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:7083386469"));
            startActivity(i);
        });

        btnSMS.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("sms:7083386469"));
            i.putExtra("sms_body", "Hello from app");
            startActivity(i);
        });

        btnEmail.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"example@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            i.putExtra(Intent.EXTRA_TEXT, "Hello Email");
            startActivity(Intent.createChooser(i, "Send Email"));
        });

        btnCamera.setOnClickListener(v -> {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(i);
        });

        btnMap.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("geo:0,0?q=Kolhapur"));
            startActivity(i);
        });
    }
}
