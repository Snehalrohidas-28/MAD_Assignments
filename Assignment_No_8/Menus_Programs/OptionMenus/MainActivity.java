package com.example.optionmenu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.mainLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.red) {
            layout.setBackgroundColor(Color.RED);
            Toast.makeText(this, "Background changed to RED", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.green) {
            layout.setBackgroundColor(Color.GREEN);
            Toast.makeText(this, "Background changed to GREEN", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.blue) {
            layout.setBackgroundColor(Color.BLUE);
            Toast.makeText(this, "Background changed to BLUE", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
