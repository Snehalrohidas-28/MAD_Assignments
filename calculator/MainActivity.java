package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText num1, num2;
    Button add, sub, mul, div, btnBack;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);

        add = findViewById(R.id.add);
        sub = findViewById(R.id.sub);
        mul = findViewById(R.id.mul);
        div = findViewById(R.id.div);

        btnBack = findViewById(R.id.btnBack);
        result = findViewById(R.id.result);


        add.setOnClickListener(v -> calculate("+"));

        sub.setOnClickListener(v -> calculate("-"));


        mul.setOnClickListener(v -> calculate("*"));


        div.setOnClickListener(v -> calculate("/"));


        btnBack.setOnClickListener(v -> goBackToMainApp());


        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        goBackToMainApp();
                    }
                });
    }


    private void calculate(String operator) {

        String n1 = num1.getText().toString().trim();
        String n2 = num2.getText().toString().trim();

        if (n1.isEmpty() || n2.isEmpty()) {
            Toast.makeText(this, "Enter both numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        double a = Double.parseDouble(n1);
        double b = Double.parseDouble(n2);
        double res = 0;

        switch (operator) {
            case "+":
                res = a + b;
                break;

            case "-":
                res = a - b;
                break;

            case "*":
                res = a * b;
                break;

            case "/":
                if (b == 0) {
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                res = a / b;
                break;
        }

        result.setText("Result: " + res);
    }


    private void goBackToMainApp() {

        Intent intent = getPackageManager()
                .getLaunchIntentForPackage("com.example.grid"); 

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Main App not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
