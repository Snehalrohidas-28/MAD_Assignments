package com.example.contextmenu;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageView imgPizza, imgBurger, imgPasta;
    String selectedFood = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPizza = findViewById(R.id.imgPizza);
        imgBurger = findViewById(R.id.imgBurger);
        imgPasta = findViewById(R.id.imgPasta);


        imgPizza.setOnLongClickListener(v -> {
            selectedFood = "Pizza 🍕";
            showPopup(v);
            return true;
        });

        imgBurger.setOnLongClickListener(v -> {
            selectedFood = "Burger 🍔";
            showPopup(v);
            return true;
        });

        imgPasta.setOnLongClickListener(v -> {
            selectedFood = "Pasta 🍝";
            showPopup(v);
            return true;
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.home) {
            Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == R.id.cart) {
            Toast.makeText(this, "Cart Opened 🛒", Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == R.id.logout) {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.order) {
                Toast.makeText(this, selectedFood + " Ordered", Toast.LENGTH_SHORT).show();

            } else if (item.getItemId() == R.id.cancel) {
                Toast.makeText(this, selectedFood + " Cancelled", Toast.LENGTH_SHORT).show();

            } else if (item.getItemId() == R.id.details) {
                Toast.makeText(this, "Details of " + selectedFood, Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        popupMenu.show();
    }
}
