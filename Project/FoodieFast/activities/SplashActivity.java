package com.example.foodiefast.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodiefast.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000; // 2 seconds

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        runnable = () -> {


            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            String email = sp.getString("email", null);

            if (email != null) {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {

                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish();
        };

        handler.postDelayed(runnable, SPLASH_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
