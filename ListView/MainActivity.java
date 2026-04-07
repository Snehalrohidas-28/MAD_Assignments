package com.example.listexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;

    String[] foods = {
            "Pizza",
            "Burger",
            "Sandwich",
            "Pasta",
            "Noodles",
            "Dosa",
            "Idli",
            "Biryani",
            "Paneer Tikka",
            "Ice Cream"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewFood);
        btnBack = findViewById(R.id.btnBack);

     
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                foods
        );
        listView.setAdapter(adapter);

     
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
