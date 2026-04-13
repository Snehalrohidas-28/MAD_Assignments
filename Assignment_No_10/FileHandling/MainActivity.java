package com.example.filehandling;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;

public class MainActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etAge, etCity, etAddress, etPassword;
    RadioGroup rgGender;
    Button btnSave, btnLoad;
    TableLayout tableLayout;

    String fileName = "mydata.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etCity = findViewById(R.id.etCity);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        rgGender = findViewById(R.id.rgGender);
        tableLayout = findViewById(R.id.tableLayout);

        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);


        btnSave.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            int selectedId = rgGender.getCheckedRadioButtonId();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                    age.isEmpty() || city.isEmpty() || address.isEmpty() ||
                    password.isEmpty() || selectedId == -1) {

                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton rb = findViewById(selectedId);
            String gender = rb.getText().toString();

            String data = name + "|" + email + "|" + phone + "|" +
                    age + "|" + city + "|" + address + "|" +
                    gender + "|" + password + "\n";

            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);
                fos.write(data.getBytes());
                fos.close();

                Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();

                etName.setText(""); etEmail.setText(""); etPhone.setText("");
                etAge.setText(""); etCity.setText(""); etAddress.setText("");
                etPassword.setText(""); rgGender.clearCheck();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnLoad.setOnClickListener(v -> {

            try {
                FileInputStream fis = openFileInput(fileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                String line;

                if (tableLayout.getChildCount() > 1) {
                    tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
                }

                while ((line = br.readLine()) != null) {

                    String[] p = line.split("\\|");
                    if (p.length < 8) continue;

                    TableRow row = new TableRow(this);

                    for (int i = 0; i < 7; i++) {
                        TextView tv = new TextView(this);
                        tv.setText(p[i]);
                        tv.setPadding(10,10,10,10);
                        row.addView(tv);
                    }

                    tableLayout.addView(row);
                }

                br.close();

            } catch (Exception e) {
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
