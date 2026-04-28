package com.example.studentdata;


import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentdata.database.DBHelper;

public class MainActivity extends AppCompatActivity {

    EditText etRoll, etName, etMarks;
    Button btnInsert, btnDisplay, btnUpdate;
    TextView tvData;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etRoll = findViewById(R.id.etRoll);
        etName = findViewById(R.id.etName);
        etMarks = findViewById(R.id.etMarks);

        btnInsert = findViewById(R.id.btnInsert);
        btnDisplay = findViewById(R.id.btnDisplay);
        btnUpdate = findViewById(R.id.btnUpdate);

        tvData = findViewById(R.id.tvData);

        db = new DBHelper(this);

        btnInsert.setOnClickListener(view -> {

            String roll = etRoll.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String marks = etMarks.getText().toString().trim();

            if (roll.isEmpty() || name.isEmpty() || marks.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = db.insertData(roll, name, marks);

            if (result)
                Toast.makeText(MainActivity.this, "Record Inserted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Insert Failed (Roll may exist)", Toast.LENGTH_SHORT).show();
        });

        btnDisplay.setOnClickListener(view -> {

            Cursor cursor = db.getAllData();

            if (cursor.getCount() == 0) {
                tvData.setText("No Records Found");
                return;
            }

            StringBuilder buffer = new StringBuilder();

            while (cursor.moveToNext()) {
                buffer.append("Roll No: ").append(cursor.getString(0)).append("\n");
                buffer.append("Name: ").append(cursor.getString(1)).append("\n");
                buffer.append("Marks: ").append(cursor.getString(2)).append("\n\n");
            }

            tvData.setText(buffer.toString());
        });


        btnUpdate.setOnClickListener(view -> {

            String roll = etRoll.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String marks = etMarks.getText().toString().trim();

            if (roll.isEmpty() || name.isEmpty() || marks.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = db.updateData(roll, name, marks);

            if (result)
                Toast.makeText(MainActivity.this, "Record Updated", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Update Failed (Roll not found)", Toast.LENGTH_SHORT).show();
        });
    }
}
