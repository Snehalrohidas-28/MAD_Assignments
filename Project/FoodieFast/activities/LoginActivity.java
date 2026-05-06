package com.example.foodiefast.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodiefast.R;
import com.example.foodiefast.database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView register;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String savedEmail = sp.getString("email", null);

        if (savedEmail != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.tvRegister);

        db = new DBHelper(this);

        login.setOnClickListener(v -> loginUser());

        register.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {

        String e = email.getText().toString().trim();
        String p = password.getText().toString().trim();

        if (e.isEmpty()) {
            email.setError("Email required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.setError("Enter valid email");
            email.requestFocus();
            return;
        }

        if (p.isEmpty()) {
            password.setError("Password required");
            password.requestFocus();
            return;
        }

        if (p.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        boolean isLogin = db.loginUser(e, p);

        if (isLogin) {

            Toast.makeText(this, "Login Successful ✅", Toast.LENGTH_SHORT).show();

            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            sp.edit().putString("email", e).apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("email", e);
            startActivity(intent);

            finish();

        } else {
            Toast.makeText(this, "Invalid Email or Password ❌", Toast.LENGTH_SHORT).show();
        }
    }
}
