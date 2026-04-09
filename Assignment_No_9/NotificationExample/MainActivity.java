package com.example.notification;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    private EditText foodTitle, foodOffer;
    private Button sendBtn;

    private final String CHANNEL_ID = "foodiefast_channel";
    private int notifyId = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodTitle = findViewById(R.id.foodTitle);
        foodOffer = findViewById(R.id.foodOffer);
        sendBtn = findViewById(R.id.sendBtn);

        setupChannel();
        askPermission();

        sendBtn.setOnClickListener(v -> {
            String food = foodTitle.getText().toString().trim();
            String offer = foodOffer.getText().toString().trim();

            if (food.isEmpty() || offer.isEmpty()) {
                Toast.makeText(this, "Enter food & offer", Toast.LENGTH_SHORT).show();
                return;
            }

            pushFoodNotification(food, offer);
        });
    }

    private void setupChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "FoodieFast Offers",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Food offers & recipe updates");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        10
                );
            }
        }
    }

    private void pushFoodNotification(String food, String offer) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Enable notification permission", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = "🔥 " + food + " Available!";
        String message = "Special Offer: " + offer + " 😍";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Order now on FoodieFast and enjoy " + offer + " on delicious " + food + "! 🍽️"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(notifyId++, builder.build());

        Toast.makeText(this, "Food Notification Sent 🍔", Toast.LENGTH_SHORT).show();
    }
}
