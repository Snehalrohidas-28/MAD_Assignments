package com.example.foodiefast.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodiefast.R;
import com.example.foodiefast.database.DBHelper;
import com.example.foodiefast.models.Recipe;

public class AddRecipeActivity extends AppCompatActivity {

    ImageView imgRecipe;
    EditText etName, etIngredients, etProcedure, etYoutube;
    Spinner spCategory;
    Button btnAdd;

    Uri imageUri;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        imgRecipe = findViewById(R.id.imgRecipe);
        etName = findViewById(R.id.etName);
        etIngredients = findViewById(R.id.etIngredients);
        etProcedure = findViewById(R.id.etProcedure);
        etYoutube = findViewById(R.id.etYoutube);
        spCategory = findViewById(R.id.spCategory);
        btnAdd = findViewById(R.id.btnAdd);

        dbHelper = new DBHelper(this);

        String[] categories = {
                "Breakfast", "Lunch", "Dinner", "Snacks",
                "Dessert", "Beverages", "Vegetarian",
                "NonVeg", "Healthy", "FastFood"
        };

        spCategory.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        ));

        findViewById(R.id.btnSelectImage).setOnClickListener(v -> openGallery());

        btnAdd.setOnClickListener(v -> saveRecipe());
    }


    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == 1 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();

            getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            imgRecipe.setImageURI(imageUri);
        }
    }


    private void saveRecipe() {

        String name = etName.getText().toString().trim();
        String ing = etIngredients.getText().toString().trim();
        String pro = etProcedure.getText().toString().trim();
        String yt = etYoutube.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ing) || TextUtils.isEmpty(pro)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Select image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = String.valueOf(System.currentTimeMillis());


        String imagePath = imageUri.toString();

        Recipe recipe = new Recipe();
        recipe.id = id;
        recipe.name = name;
        recipe.category = category;
        recipe.imageUrl = imagePath;
        recipe.ingredients = ing;
        recipe.procedure = pro;
        recipe.youtubeLink = yt;
        recipe.liked = false;
        recipe.saved = false;

        dbHelper.insertRecipe(recipe);

        Toast.makeText(this, "Recipe Added Successfully", Toast.LENGTH_SHORT).show();

        finish();
    }
}
