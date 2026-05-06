package com.example.foodiefast.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodiefast.R;
import com.example.foodiefast.database.DBHelper;

public class ProfileActivity extends AppCompatActivity {

    ImageView profileImage, editImage;
    TextView tvName, tvEmail, tvPhone;

    LinearLayout menuHome, menuAbout, menuLogout;

    DBHelper dbHelper;

    private static final int PICK_IMAGE = 1;

    String userEmail;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        editImage = findViewById(R.id.editImage);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        menuHome = findViewById(R.id.menuHome);
        menuAbout = findViewById(R.id.menuAbout);
        menuLogout = findViewById(R.id.menuLogout);

        dbHelper = new DBHelper(this);


        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        userEmail = sp.getString("email", null);

        if (userEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadUserData();


        editImage.setOnClickListener(v -> openGallery());


        menuHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });


        menuAbout.setOnClickListener(v -> showAboutDialog());


        menuLogout.setOnClickListener(v -> {
            getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }


    private void loadUserData() {

        String name = dbHelper.getUserName(userEmail);
        String phone = dbHelper.getUserPhone(userEmail);
        String image = dbHelper.getProfileImage(userEmail);

        tvName.setText(name);
        tvEmail.setText(userEmail);
        tvPhone.setText(phone);


        if (image != null && !image.isEmpty()) {
            profileImage.setImageURI(Uri.parse(image));
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();


            getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            profileImage.setImageURI(imageUri);


            dbHelper.saveProfileImage(userEmail, imageUri.toString());

            Toast.makeText(this, "Profile image saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("About FoodieFast")
                .setMessage("FoodieFast is a smart and user-friendly Android food recipe application designed to help users explore, manage, and personalize their cooking experience in an organized way. The app provides a centralized platform where users can easily browse a wide variety of recipes categorized into sections like breakfast, lunch, snacks, desserts, and more. It includes a secure authentication system that allows users to register and log in with proper validation, while also maintaining session management so users do not need to log in again every time they open the app. The home dashboard offers a personalized experience with a clean interface and quick access to recipes.\n" +
                        "\n" +
                        "The application also features a powerful search functionality that enables users to find recipes instantly by name. Users can interact with recipes by liking ❤\uFE0F and saving \uD83D\uDD16 them for future reference, with all data stored locally using SQLite, ensuring offline access and persistence even after restarting the app. Additionally, FoodieFast allows users to add their own recipes by entering details such as name, category, ingredients, procedure, and media links, making it interactive and user-driven. The profile section enables users to view and update their personal details like name, email, and phone number, along with optional profile image support. The app includes a splash screen that enhances user experience by checking login status and redirecting users accordingly.\n" +
                        "\n" +
                        "\uD83D\uDD39 Key Points\n" +
                        "\uD83D\uDD10 Secure user registration and login with validation\n" +
                        "\uD83D\uDD01 Session management using SharedPreferences (auto-login)\n" +
                        "\uD83C\uDFE0 Clean and personalized home dashboard\n" +
                        "\uD83D\uDD0D Real-time recipe search functionality\n" +
                        "\uD83D\uDCC2 Categorized recipes for easy browsing\n" +
                        "❤\uFE0F Like feature to mark favorite recipes\n" +
                        "\uD83D\uDD16 Save feature to store recipes for later\n" +
                        "➕ Add your own custom recipes\n" +
                        "\uD83D\uDC64 Profile management (view & update user details)\n" +
                        "\uD83D\uDDBC\uFE0F Profile image support\n" +
                        "\uD83D\uDCBE Offline data storage using SQLite\n" +
                        "\uD83D\uDE80 Splash screen with session checking\n" +
                        "\uD83D\uDCF1 Smooth and user-friendly UI/UX")
                .setPositiveButton("OK", null)
                .show();
    }
}
