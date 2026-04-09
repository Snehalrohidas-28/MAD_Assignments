package com.example.internalstorage;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText etFile, etDiary;
    Button btnCreate, btnDelete, btnSave, btnRead;
    TextView txtOutput;

    String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFile = findViewById(R.id.etFile);
        etDiary = findViewById(R.id.etDiary);
        btnCreate = findViewById(R.id.btnCreate);
        btnDelete = findViewById(R.id.btnDelete);
        btnSave = findViewById(R.id.btnSave);
        btnRead = findViewById(R.id.btnRead);
        txtOutput = findViewById(R.id.txtOutput);


        btnCreate.setOnClickListener(v -> {
            fileName = etFile.getText().toString();

            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                fos.close();
                Toast.makeText(this, "Diary Created 📔", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnDelete.setOnClickListener(v -> {
            fileName = etFile.getText().toString();

            if (deleteFile(fileName)) {
                Toast.makeText(this, "Diary Deleted ❌", Toast.LENGTH_SHORT).show();
                txtOutput.setText("");
            } else {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            fileName = etFile.getText().toString();
            String entry = etDiary.getText().toString();

            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);
                fos.write((entry + "\n\n").getBytes());
                fos.close();

                Toast.makeText(this, "Entry Saved 📝", Toast.LENGTH_SHORT).show();
                etDiary.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        btnRead.setOnClickListener(v -> {
            fileName = etFile.getText().toString();

            try {
                FileInputStream fis = openFileInput(fileName);
                int i;
                StringBuilder data = new StringBuilder();

                while ((i = fis.read()) != -1) {
                    data.append((char) i);
                }

                fis.close();
                txtOutput.setText(data.toString());

            } catch (Exception e) {
                txtOutput.setText("No entries found 📭");
            }
        });
    }
}
