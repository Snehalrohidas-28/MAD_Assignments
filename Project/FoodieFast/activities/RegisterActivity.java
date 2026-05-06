package com.example.foodiefast.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodiefast.R;
import com.example.foodiefast.database.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, phone, password, confirm;
    Button register;
    TextView loginText;

    TextView tvNameError, tvEmailError, tvPhoneError, tvPasswordError, tvConfirmError;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        db = new DBHelper(this);


        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        phone = findViewById(R.id.etPhone);
        password = findViewById(R.id.etPassword);
        confirm = findViewById(R.id.etConfirm);

        register = findViewById(R.id.btnRegister);
        loginText = findViewById(R.id.tvLogin);

        tvNameError = findViewById(R.id.tvNameError);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPhoneError = findViewById(R.id.tvPhoneError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvConfirmError = findViewById(R.id.tvConfirmError);


        register.setOnClickListener(v -> registerUser());

        loginText.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void registerUser() {

        String n = name.getText().toString().trim();
        String e = email.getText().toString().trim();
        String p = phone.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String cpass = confirm.getText().toString().trim();

        clearErrors();

        boolean isValid = true;


        if (n.isEmpty() || !n.matches("[a-zA-Z ]+")) {
            tvNameError.setText("Enter valid name");
            tvNameError.setVisibility(View.VISIBLE);
            isValid = false;
        }


        if (e.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            tvEmailError.setText("Enter valid email");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (p.length() != 10 || !p.matches("[0-9]+")) {
            tvPhoneError.setText("Enter valid 10 digit phone number");
            tvPhoneError.setVisibility(View.VISIBLE);
            isValid = false;
        }


        if (pass.length() < 6) {
            tvPasswordError.setText("Password must be at least 6 characters");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }


        if (!pass.equals(cpass)) {
            tvConfirmError.setText("Passwords do not match");
            tvConfirmError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!isValid) return;


        if (db.checkUserExists(e)) {
            Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
            return;
        }


        boolean success = db.registerUser(n, e, p, pass);

        if (success) {

            Toast.makeText(this, "Registered Successfully ✅ Please Login", Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("email", e); // optional
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Registration failed ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrors() {
        tvNameError.setVisibility(View.GONE);
        tvEmailError.setVisibility(View.GONE);
        tvPhoneError.setVisibility(View.GONE);
        tvPasswordError.setVisibility(View.GONE);
        tvConfirmError.setVisibility(View.GONE);
    }
}
