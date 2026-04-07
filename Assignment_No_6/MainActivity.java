package com.example.inputcontrols;


import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    CheckBox checkCoding, checkDesign;
    Spinner spinner;
    ToggleButton toggle;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = findViewById(R.id.radioGroupCourse);
        checkCoding = findViewById(R.id.checkCoding);
        checkDesign = findViewById(R.id.checkDesign);
        spinner = findViewById(R.id.spinnerCountry);
        toggle = findViewById(R.id.toggleUpdates);
        btnRegister = findViewById(R.id.btnRegister);

    
        String[] countries = {"India", "USA", "UK"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, countries);
        spinner.setAdapter(adapter);

      
        btnRegister.setOnClickListener(v -> {

            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton course = findViewById(selectedId);
            String courseName = course.getText().toString();

            String skills = "";
            if (checkCoding.isChecked()) skills += "Coding ";
            if (checkDesign.isChecked()) skills += "Design ";

            String country = spinner.getSelectedItem().toString();
            String updates = toggle.isChecked() ? "Yes" : "No";

            String result = "Course: " + courseName +
                    "\nSkills: " + skills +
                    "\nCountry: " + country +
                    "\nUpdates: " + updates;

            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
        });
    }
}
