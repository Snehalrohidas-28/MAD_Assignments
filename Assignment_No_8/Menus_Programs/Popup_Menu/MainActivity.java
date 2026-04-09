package com.example.popupmenu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(this, btnMenu);

            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.order) {
                    Toast.makeText(this, "Food Ordered 🍕", Toast.LENGTH_SHORT).show();

                } else if (item.getItemId() == R.id.share) {
                    Toast.makeText(this, "Shared with friends 📤", Toast.LENGTH_SHORT).show();

                } else if (item.getItemId() == R.id.favorite) {
                    Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show();
                }

                return true;
            });

            popupMenu.show();
        });
    }
}
