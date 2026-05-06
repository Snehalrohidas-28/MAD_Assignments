package com.example.foodiefast.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodiefast.R;
import com.example.foodiefast.database.DBHelper;

public class RecipeDetailActivity extends AppCompatActivity {

    ImageView imgRecipe, btnLike, btnSave, btnShare;
    TextView tvTitle, tvIngredients, tvProcedure, tvCount, btnPlus, btnMinus;
    Button btnYoutube;

    int count = 1;
    boolean isLiked, isSaved;

    String id, name, ingredients, procedure, youtube, imageUrl;
    int image;

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();


        imgRecipe = findViewById(R.id.imgRecipe);
        tvTitle = findViewById(R.id.tvTitle);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvProcedure = findViewById(R.id.tvProcedure);
        tvCount = findViewById(R.id.tvCount);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);

        btnLike = findViewById(R.id.btnLike);
        btnSave = findViewById(R.id.btnSave);
        btnShare = findViewById(R.id.btnShare);
        btnYoutube = findViewById(R.id.btnYoutube);


        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        image = getIntent().getIntExtra("image", 0);
        imageUrl = getIntent().getStringExtra("imageUrl");
        ingredients = getIntent().getStringExtra("ingredients");
        procedure = getIntent().getStringExtra("procedure");
        youtube = getIntent().getStringExtra("youtube");
        isLiked = getIntent().getBooleanExtra("liked", false);
        isSaved = getIntent().getBooleanExtra("saved", false);

        if (ingredients == null) ingredients = "";
        if (procedure == null) procedure = "";
        if (youtube == null) youtube = "";

        tvTitle.setText(name);


        if (imageUrl != null && !imageUrl.isEmpty()) {
            imgRecipe.setImageURI(Uri.parse(imageUrl));
        } else if (image != 0) {
            imgRecipe.setImageResource(image);
        } else {
            imgRecipe.setImageResource(R.drawable.ic_launcher_background);
        }

        updateUI();


        btnPlus.setOnClickListener(v -> {
            count++;
            tvCount.setText(String.valueOf(count));
            updateIngredients();
        });


        btnMinus.setOnClickListener(v -> {
            if (count > 1) {
                count--;
                tvCount.setText(String.valueOf(count));
                updateIngredients();
            }
        });

        btnLike.setOnClickListener(v -> {

            isLiked = !isLiked;

            if (isLiked) {
                db.execSQL("INSERT OR IGNORE INTO liked(recipeId) VALUES(?)",
                        new Object[]{id});
            } else {
                db.execSQL("DELETE FROM liked WHERE recipeId=?",
                        new Object[]{id});
            }

            updateUI();
        });

        btnSave.setOnClickListener(v -> {

            isSaved = !isSaved;

            if (isSaved) {
                db.execSQL("INSERT OR IGNORE INTO saved(recipeId) VALUES(?)",
                        new Object[]{id});
            } else {
                db.execSQL("DELETE FROM saved WHERE recipeId=?",
                        new Object[]{id});
            }

            updateUI();
        });

        btnShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT,
                    name + "\n\n" + ingredients + "\n\n" + procedure);
            startActivity(Intent.createChooser(share, "Share via"));
        });

        btnYoutube.setOnClickListener(v -> {
            if (!youtube.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtube)));
            } else {
                Toast.makeText(this, "No video available", Toast.LENGTH_SHORT).show();
            }
        });

        updateIngredients();
    }

    private void updateUI() {
        btnLike.setImageResource(isLiked ?
                R.drawable.ic_heart_filled :
                R.drawable.ic_heart_outline);

        btnSave.setImageResource(isSaved ?
                R.drawable.ic_bookmark_filled :
                R.drawable.ic_bookmark_outline);
    }

    private void updateIngredients() {
        String[] lines = ingredients.split("\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            result.append("• ").append(line)
                    .append(" x").append(count).append("\n");
        }

        tvIngredients.setText(result.toString());
        tvProcedure.setText(procedure);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("position",
                getIntent().getIntExtra("position", -1));
        intent.putExtra("liked", isLiked);
        intent.putExtra("saved", isSaved);

        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
