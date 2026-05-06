package com.example.foodiefast.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.foodiefast.R;
import com.example.foodiefast.adapters.RecipeAdapter;
import com.example.foodiefast.database.DBHelper;
import com.example.foodiefast.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText search;
    TextView tvHello;
    ImageView imgProfile;
    DrawerLayout drawerLayout;

    DBHelper dbHelper;
    SQLiteDatabase db;

    List<Recipe> recipeList = new ArrayList<>();
    List<Recipe> filteredList = new ArrayList<>();

    RecipeAdapter adapter;

    String currentCategory = "All";
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listRecipes);
        search = findViewById(R.id.etSearch);
        tvHello = findViewById(R.id.tvHello);
        imgProfile = findViewById(R.id.imgProfile);
        drawerLayout = findViewById(R.id.drawerLayout);

        LinearLayout menuProfile = findViewById(R.id.menuProfile);
        LinearLayout menuLogout = findViewById(R.id.menuLogout);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        userEmail = sp.getString("email", null);

        if (userEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();


        Cursor cUser = db.rawQuery("SELECT name FROM users WHERE email=?", new String[]{userEmail});
        if (cUser.moveToFirst()) {
            tvHello.setText("Hello " + cUser.getString(0) + " 👋");
        } else {
            tvHello.setText("Hello User 👋");
        }
        cUser.close();


        findViewById(R.id.navAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddRecipeActivity.class)));


        imgProfile.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(android.view.Gravity.RIGHT)) {
                drawerLayout.closeDrawer(android.view.Gravity.RIGHT);
            } else {
                drawerLayout.openDrawer(android.view.Gravity.RIGHT);
            }
        });


        menuProfile.setOnClickListener(v -> {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("email", userEmail);
            startActivity(i);
        });


        menuLogout.setOnClickListener(v -> {
            getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });


        loadRecipes();
        loadUserRecipes();
        loadLocalLikesSaved();

        filteredList.addAll(recipeList);

        adapter = new RecipeAdapter(this, filteredList);
        listView.setAdapter(adapter);

        applyFilters();

        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {}
        });

        setupCategoryClicks();

        findViewById(R.id.navHome).setOnClickListener(v -> {
            currentCategory = "All";
            applyFilters();
        });

        findViewById(R.id.navLike).setOnClickListener(v -> showLiked());
        findViewById(R.id.navSave).setOnClickListener(v -> showSaved());
    }

    private void loadUserRecipes() {
        Cursor c = db.rawQuery("SELECT * FROM recipes", null);

        while (c.moveToNext()) {
            Recipe r = new Recipe(
                    c.getString(0),
                    c.getString(1),
                    c.getString(2),
                    0,
                    c.getString(4),
                    c.getString(5),
                    c.getString(6)
            );
            r.imageUrl = c.getString(3);
            recipeList.add(r);
        }
        c.close();
    }

    private void loadLocalLikesSaved() {

        Cursor liked = db.rawQuery("SELECT recipeId FROM liked", null);
        while (liked.moveToNext()) {
            String id = liked.getString(0);
            for (Recipe r : recipeList) {
                if (r.id.equals(id)) r.liked = true;
            }
        }
        liked.close();

        Cursor saved = db.rawQuery("SELECT recipeId FROM saved", null);
        while (saved.moveToNext()) {
            String id = saved.getString(0);
            for (Recipe r : recipeList) {
                if (r.id.equals(id)) r.saved = true;
            }
        }
        saved.close();
    }

    private void applyFilters() {
        String text = search.getText().toString().toLowerCase();
        filteredList.clear();

        for (Recipe r : recipeList) {
            boolean matchCategory =
                    currentCategory.equals("All") ||
                            r.category.equalsIgnoreCase(currentCategory);

            boolean matchSearch =
                    r.name.toLowerCase().contains(text);

            if (matchCategory && matchSearch) {
                filteredList.add(r);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void showLiked() {
        filteredList.clear();
        for (Recipe r : recipeList) {
            if (r.liked) filteredList.add(r);
        }
        adapter.notifyDataSetChanged();
    }

    private void showSaved() {
        filteredList.clear();
        for (Recipe r : recipeList) {
            if (r.saved) filteredList.add(r);
        }
        adapter.notifyDataSetChanged();
    }

    private void setupCategoryClicks() {
        findViewById(R.id.catAll).setOnClickListener(v -> setCategory("All"));
        findViewById(R.id.catBreakfast).setOnClickListener(v -> setCategory("Breakfast"));
        findViewById(R.id.catLunch).setOnClickListener(v -> setCategory("Lunch"));
        findViewById(R.id.catDinner).setOnClickListener(v -> setCategory("Dinner"));
        findViewById(R.id.catSnacks).setOnClickListener(v -> setCategory("Snacks"));
        findViewById(R.id.catDessert).setOnClickListener(v -> setCategory("Dessert"));
        findViewById(R.id.catBeverages).setOnClickListener(v -> setCategory("Beverages"));
        findViewById(R.id.catVeg).setOnClickListener(v -> setCategory("Vegetarian"));
        findViewById(R.id.catNonVeg).setOnClickListener(v -> setCategory("NonVeg"));
        findViewById(R.id.catHealthy).setOnClickListener(v -> setCategory("Healthy"));
        findViewById(R.id.catFast).setOnClickListener(v -> setCategory("FastFood"));
    }

    private void setCategory(String category) {
        currentCategory = category;
        applyFilters();
    }

    private void loadRecipes() {

        recipeList.clear();

        // 1️⃣ Vegetable Upma
        recipeList.add(new Recipe(
                "B1",
                "Vegetable Upma",
                "Breakfast",
                R.drawable.upma,
                "Semolina (Rava)\n1 cup semolina\n2 cups water\n\nVegetables\n1 onion chopped\n1 green chili\n\nTempering\n1 tsp mustard seeds\nCurry leaves\n\nOthers\n2 tbsp oil\nSalt to taste",
                "1. Heat a pan and dry roast semolina on low flame until aromatic.\n" +
                        "2. Heat oil in another pan and add mustard seeds.\n" +
                        "3. Once they splutter, add curry leaves and green chilies.\n" +
                        "4. Add chopped onions and sauté until soft.\n" +
                        "5. Add water and salt, bring to boil.\n" +
                        "6. Slowly add roasted semolina while stirring continuously.\n" +
                        "7. Cook on low flame till thick.\n" +
                        "8. Cover and cook for 2 minutes.\n" +
                        "9. Serve hot.",
                "https://www.youtube.com/results?search_query=veg+upma"
        ));

        // 2️⃣ Poha
        recipeList.add(new Recipe(
                "B2",
                "Poha",
                "Breakfast",
                R.drawable.poha,
                "2 cups poha\n1 onion chopped\n1 potato chopped\nMustard seeds\nCurry leaves\nTurmeric\nSalt\nLemon juice",
                "1. Wash poha and keep aside.\n" +
                        "2. Heat oil and add mustard seeds.\n" +
                        "3. Add curry leaves and onions.\n" +
                        "4. Add potatoes and cook till soft.\n" +
                        "5. Add turmeric and salt.\n" +
                        "6. Add poha and mix gently.\n" +
                        "7. Cook for 3–4 minutes.\n" +
                        "8. Add lemon juice and serve.",
                "https://www.youtube.com/results?search_query=poha"
        ));

        // 3️⃣ Bread Omelette
        recipeList.add(new Recipe(
                "B3",
                "Bread Omelette",
                "Breakfast",
                R.drawable.breadomelet,
                "2 eggs\n2 bread slices\n1 onion\n1 green chili\nSalt\nPepper\nButter",
                "1. Break eggs into a bowl and whisk well.\n" +
                        "2. Add chopped onion, chili, salt and pepper.\n" +
                        "3. Heat butter in pan.\n" +
                        "4. Pour egg mixture evenly.\n" +
                        "5. Place bread slices on top.\n" +
                        "6. Flip carefully and cook both sides.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/results?search_query=bread+omelette"
        ));

        // 4️⃣ Vegetable Sandwich
        recipeList.add(new Recipe(
                "B4",
                "Vegetable Sandwich",
                "Breakfast",
                R.drawable.veg_sandwich,
                "Bread slices\nButter\nGreen chutney\nCucumber\nTomato\nBoiled potato\nSalt\nChaat masala",
                "1. Apply butter and chutney on bread.\n" +
                        "2. Place vegetables evenly.\n" +
                        "3. Sprinkle salt and masala.\n" +
                        "4. Cover with another slice.\n" +
                        "5. Grill or toast until crispy.\n" +
                        "6. Cut and serve.",
                "https://www.youtube.com/results?search_query=veg+sandwich"
        ));

        // 5️⃣ Aloo Paratha
        recipeList.add(new Recipe(
                "B5",
                "Aloo Paratha",
                "Breakfast",
                R.drawable.aloo_paratha,
                "Wheat flour dough\nBoiled potatoes\nGreen chili\nCoriander\nSalt\nSpices\nOil",
                "1. Mash potatoes and mix spices.\n" +
                        "2. Roll dough into small circle.\n" +
                        "3. Add stuffing and seal.\n" +
                        "4. Roll gently into paratha.\n" +
                        "5. Cook on tawa with oil.\n" +
                        "6. Flip and cook both sides.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/results?search_query=aloo+paratha"
        ));

        // 6️⃣ Idli
        recipeList.add(new Recipe(
                "B6",
                "Idli",
                "Breakfast",
                R.drawable.idli,
                "2 cups rice\n1 cup urad dal\nSalt\nWater",
                "1. Soak rice and dal separately for 6 hours.\n" +
                        "2. Grind into smooth batter.\n" +
                        "3. Mix and ferment overnight.\n" +
                        "4. Pour into idli molds.\n" +
                        "5. Steam for 10–12 minutes.\n" +
                        "6. Serve hot.",
                "https://www.youtube.com/results?search_query=idli"
        ));

        // 7️⃣ Dosa
        recipeList.add(new Recipe(
                "B7",
                "Dosa",
                "Breakfast",
                R.drawable.masala_dosa,
                "Rice batter\nSalt\nOil",
                "1. Heat tawa.\n" +
                        "2. Pour batter and spread thin.\n" +
                        "3. Add oil around edges.\n" +
                        "4. Cook until crispy.\n" +
                        "5. Fold and serve.",
                "https://www.youtube.com/results?search_query=dosa"
        ));


//Lunch
        // 🍋 1. Lemon Rice
        recipeList.add(new Recipe(
                "L1",
                "Lemon Rice",
                "Lunch",
                R.drawable.lemon_rice,
                "2 cups cooked rice\n2 tbsp oil\n1 tsp mustard seeds\n1 tsp urad dal\n2 dried red chilies\nCurry leaves\n2 tbsp peanuts\n½ tsp turmeric\nSalt\nJuice of 1 lemon\nCoriander leaves",
                "1. Heat oil in a pan on medium flame.\n" +
                        "2. Add mustard seeds and allow them to crackle.\n" +
                        "3. Add urad dal and fry until light golden.\n" +
                        "4. Add peanuts and roast until crunchy.\n" +
                        "5. Add dried red chilies and curry leaves.\n" +
                        "6. Add turmeric powder and mix well.\n" +
                        "7. Add cooked rice and salt.\n" +
                        "8. Mix gently without breaking rice grains.\n" +
                        "9. Turn off flame and add fresh lemon juice.\n" +
                        "10. Garnish with coriander leaves and serve.",
                "https://www.youtube.com/results?search_query=lemon+rice"
        ));

// 🍛 2. Vegetable Pulao
        recipeList.add(new Recipe(
                "L2",
                "Vegetable Pulao",
                "Lunch",
                R.drawable.veg_pulao,
                "1 cup basmati rice\n2 cups water\nMixed vegetables\n1 onion sliced\n1 bay leaf\n3 cloves\n1 cinnamon stick\n2 cardamom\nSalt\n2 tbsp oil",
                "1. Wash and soak rice for 20 minutes.\n" +
                        "2. Heat oil in a pressure cooker.\n" +
                        "3. Add bay leaf, cloves, cinnamon, and cardamom.\n" +
                        "4. Sauté sliced onions until golden brown.\n" +
                        "5. Add mixed vegetables and cook for 2 minutes.\n" +
                        "6. Add soaked rice and mix gently.\n" +
                        "7. Add water and salt.\n" +
                        "8. Close lid and cook for 2 whistles.\n" +
                        "9. Let pressure release naturally.\n" +
                        "10. Fluff rice and serve hot.",
                "https://www.youtube.com/results?search_query=veg+pulao"
        ));

// 🍅 3. Tomato Rice
        recipeList.add(new Recipe(
                "L3",
                "Tomato Rice",
                "Lunch",
                R.drawable.tomato_rice,
                "2 cups cooked rice\n2 tomatoes chopped\n1 onion\n1 tsp mustard seeds\nCurry leaves\n½ tsp turmeric\n1 tsp chili powder\nSalt\nOil",
                "1. Heat oil in a pan.\n" +
                        "2. Add mustard seeds and let them splutter.\n" +
                        "3. Add curry leaves and chopped onions.\n" +
                        "4. Sauté until onions turn soft.\n" +
                        "5. Add chopped tomatoes and cook until mushy.\n" +
                        "6. Add turmeric, chili powder, and salt.\n" +
                        "7. Mix well and cook for 2 minutes.\n" +
                        "8. Add cooked rice and mix gently.\n" +
                        "9. Cook for 3–4 minutes.\n" +
                        "10. Serve hot.",
                "https://www.youtube.com/results?search_query=tomato+rice"
        ));

// 🧀 4. Paneer Fried Rice
        recipeList.add(new Recipe(
                "L4",
                "Paneer Fried Rice",
                "Lunch",
                R.drawable.paneer_fried_rice,
                "Cooked rice\nPaneer cubes\nCapsicum\nCarrot\nSoy sauce\nGarlic\nPepper\nOil",
                "1. Heat oil in a wok.\n" +
                        "2. Add chopped garlic and sauté.\n" +
                        "3. Add chopped vegetables and stir fry.\n" +
                        "4. Add paneer cubes and cook lightly.\n" +
                        "5. Add cooked rice and mix well.\n" +
                        "6. Add soy sauce and pepper.\n" +
                        "7. Toss everything properly.\n" +
                        "8. Cook for 2–3 minutes.\n" +
                        "9. Adjust seasoning.\n" +
                        "10. Serve hot.",
                "https://www.youtube.com/results?search_query=paneer+fried+rice"
        ));

// 🥪 5. Vegetable Sandwich
        recipeList.add(new Recipe(
                "L5",
                "Vegetable Sandwich",
                "Lunch",
                R.drawable.veg_sandwich,
                "Bread slices\nButter\nBoiled potatoes\nCucumber\nTomato\nSalt\nChaat masala",
                "1. Take bread slices and apply butter.\n" +
                        "2. Place boiled potato slices.\n" +
                        "3. Add cucumber and tomato slices.\n" +
                        "4. Sprinkle salt and chaat masala.\n" +
                        "5. Cover with another bread slice.\n" +
                        "6. Press gently.\n" +
                        "7. Cut into halves.\n" +
                        "8. Optional: Grill for crispy texture.\n" +
                        "9. Serve fresh.\n" +
                        "10. Pack for lunch if needed.",
                "https://www.youtube.com/results?search_query=veg+sandwich"
        ));

// 🍚 6. Curd Rice
        recipeList.add(new Recipe(
                "L6",
                "Curd Rice",
                "Lunch",
                R.drawable.curd_rice,
                "Cooked rice\nCurd\nMustard seeds\nCurry leaves\nGinger\nGreen chili\nSalt",
                "1. Mash cooked rice slightly.\n" +
                        "2. Add curd and mix well.\n" +
                        "3. Add salt to taste.\n" +
                        "4. Heat oil for tempering.\n" +
                        "5. Add mustard seeds.\n" +
                        "6. Add curry leaves, ginger, and chili.\n" +
                        "7. Pour tempering over rice.\n" +
                        "8. Mix well.\n" +
                        "9. Chill or serve fresh.\n" +
                        "10. Garnish if desired.",
                "https://www.youtube.com/results?search_query=curd+rice"
        ));



        // 🧀 1. Paneer Butter Masala
        recipeList.add(new Recipe(
                "L21",
                "Paneer Butter Masala",
                "Lunch",
                R.drawable.paneer_butter,
                "200g paneer cubes\n" +
                        "2 medium tomatoes (pureed)\n" +
                        "1 large onion (finely chopped)\n" +
                        "1 tbsp butter\n" +
                        "1 tbsp oil\n" +
                        "1 tsp ginger garlic paste\n" +
                        "1/2 cup fresh cream\n" +
                        "1 tsp garam masala\n" +
                        "1 tsp red chili powder\n" +
                        "1/2 tsp turmeric\n" +
                        "1 tsp sugar\n" +
                        "Salt to taste\n" +
                        "Fresh coriander leaves",
                "1. Heat oil and butter in a pan.\n" +
                        "2. Add chopped onions and sauté until golden brown.\n" +
                        "3. Add ginger garlic paste and cook for 1 minute.\n" +
                        "4. Add tomato puree and cook until oil separates.\n" +
                        "5. Add turmeric, chili powder, salt and sugar.\n" +
                        "6. Add paneer cubes and mix gently.\n" +
                        "7. Pour cream and mix well.\n" +
                        "8. Simmer for 5–7 minutes.\n" +
                        "9. Sprinkle garam masala.\n" +
                        "10. Garnish with coriander and serve hot with roti or naan.",
                "https://www.youtube.com/results?search_query=paneer+butter+masala"
        ));


// 🍔 2. Veg Burger
        recipeList.add(new Recipe(
                "L22",
                "Veg Burger",
                "Lunch",
                R.drawable.veg_burger,
                "2 burger buns\n" +
                        "2 potato patties\n" +
                        "2 lettuce leaves\n" +
                        "1 tomato (sliced)\n" +
                        "1 onion (sliced)\n" +
                        "2 cheese slices\n" +
                        "2 tbsp mayonnaise\n" +
                        "1 tbsp butter",
                "1. Heat a pan and apply butter to burger buns.\n" +
                        "2. Toast buns until lightly crispy.\n" +
                        "3. Cook potato patties until golden brown.\n" +
                        "4. Spread mayonnaise on buns.\n" +
                        "5. Place lettuce, tomato and onion slices.\n" +
                        "6. Add patty and cheese slice.\n" +
                        "7. Cover with top bun.\n" +
                        "8. Press gently and serve hot.",
                "https://www.youtube.com/results?search_query=veg+burger"
        ));


// 🌯 3. Paneer Kathi Roll
        recipeList.add(new Recipe(
                "L23",
                "Paneer Kathi Roll",
                "Lunch",
                R.drawable.kathi_roll,
                "2 chapatis\n" +
                        "150g paneer (cut into strips)\n" +
                        "1 onion (sliced)\n" +
                        "1 capsicum (sliced)\n" +
                        "1 tsp red chili powder\n" +
                        "1/2 tsp garam masala\n" +
                        "2 tbsp oil\n" +
                        "2 tbsp tomato sauce\n" +
                        "Salt",
                "1. Heat oil in a pan.\n" +
                        "2. Add onion and capsicum, sauté for 2 minutes.\n" +
                        "3. Add paneer and spices.\n" +
                        "4. Cook for 5 minutes.\n" +
                        "5. Place filling on chapati.\n" +
                        "6. Add sauce and raw onions.\n" +
                        "7. Roll tightly.\n" +
                        "8. Serve hot or pack.",
                "https://www.youtube.com/results?search_query=paneer+kathi+roll"
        ));


// 🍝 4. White Sauce Pasta
        recipeList.add(new Recipe(
                "L24",
                "White Sauce Pasta",
                "Lunch",
                R.drawable.white_pasta,
                "1 cup pasta\n" +
                        "2 cups milk\n" +
                        "2 tbsp butter\n" +
                        "2 tbsp flour\n" +
                        "1 tsp garlic (chopped)\n" +
                        "1/2 cup mixed vegetables\n" +
                        "1/2 cup grated cheese\n" +
                        "Salt\n" +
                        "Black pepper",
                "1. Boil pasta in salted water until soft.\n" +
                        "2. Heat butter and add garlic.\n" +
                        "3. Add flour and cook for 1 minute.\n" +
                        "4. Slowly add milk while stirring.\n" +
                        "5. Add vegetables and cook.\n" +
                        "6. Add cheese, salt and pepper.\n" +
                        "7. Add boiled pasta and mix.\n" +
                        "8. Cook for 2–3 minutes.\n" +
                        "9. Serve hot.",
                "https://www.youtube.com/results?search_query=white+sauce+pasta"
        ));




// 🥪 7. Cheese Corn Sandwich
        recipeList.add(new Recipe(
                "L27",
                "Cheese Corn Sandwich",
                "Lunch",
                R.drawable.corn_sandwich,
                "4 bread slices\n" +
                        "1/2 cup sweet corn\n" +
                        "1/2 cup grated cheese\n" +
                        "1 tbsp butter\n" +
                        "Salt\n" +
                        "Black pepper",
                "1. Mix corn, cheese, salt and pepper.\n" +
                        "2. Spread mixture on bread.\n" +
                        "3. Cover with another slice.\n" +
                        "4. Apply butter outside.\n" +
                        "5. Grill until golden.\n" +
                        "6. Cut and serve.",
                "https://www.youtube.com/results?search_query=corn+cheese+sandwich"
        ));



//  DINNER RECIPES

// 1️⃣ Paneer Butter Masala
        recipeList.add(new Recipe(
                "D1",
                "Paneer Butter Masala",
                "Dinner",
                R.drawable.paneer_butter_masala,
                "200g paneer cubes\n2 tomatoes (pureed)\n1 onion finely chopped\n10 cashews soaked\n2 tbsp butter\n1 tbsp oil\n1 tsp ginger garlic paste\n1 tsp red chili powder\n½ tsp turmeric\n1 tsp coriander powder\n1 tsp garam masala\n½ cup fresh cream\nSalt to taste\nCoriander leaves",
                "1. Heat oil and butter in a pan.\n2. Add chopped onions and sauté until golden brown.\n3. Add ginger garlic paste and cook for 1 minute.\n4. Add tomato puree and cook until thick.\n5. Grind cashews and add paste to gravy.\n6. Add spices and cook until oil separates.\n7. Add paneer cubes and mix gently.\n8. Add cream and simmer for 3–5 minutes.\n9. Garnish with coriander and serve hot.",
                "https://www.youtube.com/results?search_query=paneer+butter+masala"
        ));

// 2️⃣ Dal Makhani
        recipeList.add(new Recipe(
                "D2",
                "Dal Makhani",
                "Dinner",
                R.drawable.dal_makhani,
                "1 cup whole black urad dal\n¼ cup rajma\n3 tbsp butter\n1 cup tomato puree\n1 tsp ginger garlic paste\n½ tsp red chili powder\n½ cup cream\nSalt",
                "1. Soak dal and rajma overnight.\n2. Pressure cook until soft.\n3. Heat butter and sauté garlic paste.\n4. Add tomato puree and cook well.\n5. Add cooked dal and mix.\n6. Simmer on low flame for 25–30 minutes.\n7. Add cream and cook 5 minutes.\n8. Serve hot.",
                "https://www.youtube.com/results?search_query=dal+makhani"
        ));
// 3️⃣ Palak Paneer
        recipeList.add(new Recipe(
                "D3","Palak Paneer","Dinner",R.drawable.palak_paneer,
                "2 cups spinach leaves\n200g paneer\n1 onion\n1 tomato\n4 garlic cloves\n1 tsp cumin seeds\n1 tsp garam masala\nSalt",
                "1. Wash and boil spinach for 2 minutes.\n2. Cool and grind into puree.\n3. Heat oil and add cumin seeds.\n4. Add onion and garlic, sauté till soft.\n5. Add tomato and cook till mushy.\n6. Add spinach puree.\n7. Add paneer cubes.\n8. Cook for 5 minutes and serve.",
                "https://www.youtube.com/results?search_query=palak+paneer"
        ));

// 4️⃣ Vegetable Jalfrezi
        recipeList.add(new Recipe(
                "D4","Vegetable Jalfrezi","Dinner",R.drawable.jalfrezi,
                "1 cup mixed vegetables (carrot, beans, capsicum)\n1 onion sliced\n1 tomato chopped\n2 tbsp oil\n1 tsp chili powder\n½ tsp turmeric\nSalt",
                "1. Heat oil in a pan.\n2. Add onions and sauté.\n3. Add vegetables and cook for 5–7 minutes.\n4. Add tomatoes and spices.\n5. Cook until vegetables are tender.\n6. Serve hot.",
                "https://www.youtube.com/results?search_query=veg+jalfrezi"
        ));

// 5️⃣ Chicken Biryani
        recipeList.add(new Recipe(
                "D5","Chicken Biryani","Dinner",R.drawable.biryani,
                "500g chicken\n2 cups basmati rice\n1 cup yogurt\n2 onions sliced\nWhole spices\nMint leaves\nSalt",
                "1. Marinate chicken with yogurt and spices for 1 hour.\n2. Cook rice until 70% done.\n3. Layer rice and chicken.\n4. Add fried onions and mint.\n5. Cover and cook on low flame for 20 minutes.\n6. Serve hot.",
                "https://www.youtube.com/results?search_query=chicken+biryani"
        ));
// 6️⃣ Chana Masala
        recipeList.add(new Recipe(
                "D6","Chana Masala","Dinner",R.drawable.chana_masala,
                "1 cup dried chickpeas (soaked overnight)\n2 tomatoes finely chopped\n1 onion finely chopped\n1 tsp ginger garlic paste\n2 tbsp oil\n1 tsp cumin seeds\n1 tsp coriander powder\n½ tsp turmeric\n1 tsp red chili powder\n1 tsp garam masala\nSalt\nFresh coriander",
                "1. Soak chickpeas overnight and pressure cook until soft.\n2. Heat oil in a pan and add cumin seeds.\n3. Add onions and sauté until golden.\n4. Add ginger garlic paste and cook.\n5. Add tomatoes and cook until soft.\n6. Add spices and cook well.\n7. Add boiled chickpeas and mix.\n8. Simmer for 10–15 minutes.\n9. Garnish with coriander and serve hot.",
                "https://www.youtube.com/results?search_query=chana+masala"
        ));


// 12️⃣ Shahi Paneer (Veg)
        recipeList.add(new Recipe(
                "D22",
                "Shahi Paneer",
                "Dinner",
                R.drawable.shahi_paneer,
                "200g paneer cubes\n" +
                        "2 onions (sliced)\n" +
                        "2 tomatoes (pureed)\n" +
                        "10-12 cashews (soaked)\n" +
                        "2 tbsp fresh cream\n" +
                        "2 tbsp oil\n" +
                        "1 tbsp butter\n" +
                        "1 tsp ginger garlic paste\n" +
                        "1 tsp red chili powder\n" +
                        "½ tsp turmeric powder\n" +
                        "1 tsp coriander powder\n" +
                        "1 tsp garam masala\n" +
                        "1 tsp sugar\n" +
                        "Salt to taste\n" +
                        "Fresh coriander leaves",

                "1. Heat oil and butter in a pan.\n" +
                        "2. Add sliced onions and sauté until golden brown.\n" +
                        "3. Add ginger garlic paste and cook for 1 minute.\n" +
                        "4. Add tomato puree and cook until thick.\n" +
                        "5. Grind soaked cashews into a smooth paste and add to gravy.\n" +
                        "6. Add red chili powder, turmeric, coriander powder and salt.\n" +
                        "7. Cook until oil starts separating from the gravy.\n" +
                        "8. Add paneer cubes and mix gently.\n" +
                        "9. Add fresh cream and a little sugar.\n" +
                        "10. Simmer for 5 minutes on low flame.\n" +
                        "11. Garnish with coriander leaves.\n" +
                        "12. Serve hot with naan, roti or rice.",

                "https://www.youtube.com/results?search_query=shahi+paneer+recipe"
        ));

// 13️⃣ Grilled Chicken
        recipeList.add(new Recipe(
                "D13","Grilled Chicken","Dinner",R.drawable.grilled_chicken,
                "Chicken pieces\nSpices\nOil\nSalt",
                "1. Marinate chicken with spices.\n2. Keep for 30 minutes.\n3. Grill on medium heat.\n4. Cook until done.\n5. Serve hot.",
                "https://www.youtube.com/watch?v=A3lcRok1zf8"
        ));



        // 1️⃣ Samosa
        recipeList.add(new Recipe(
                "S1","Samosa","Snacks",R.drawable.samosa,
                "2 cups all-purpose flour (maida)\n" +
                        "4 tbsp oil or ghee\n" +
                        "Salt to taste\n" +
                        "Water (for kneading)\n" +
                        "3 boiled potatoes (mashed)\n" +
                        "1/2 cup green peas\n" +
                        "1 tsp cumin seeds\n" +
                        "1 tsp coriander powder\n" +
                        "1 tsp garam masala\n" +
                        "1 tsp red chili powder\n" +
                        "1 tbsp chopped coriander leaves\n" +
                        "Oil for deep frying",

                "1. In a bowl, mix flour, salt and oil. Rub well to form crumbs.\n" +
                        "2. Add water gradually and knead into stiff dough. Rest for 20 minutes.\n" +
                        "3. Heat oil in a pan, add cumin seeds.\n" +
                        "4. Add green peas and cook for 2 minutes.\n" +
                        "5. Add mashed potatoes, spices and salt. Mix well.\n" +
                        "6. Add coriander leaves and let stuffing cool.\n" +
                        "7. Divide dough into small balls and roll into oval shapes.\n" +
                        "8. Cut into halves and form cone.\n" +
                        "9. Fill stuffing and seal edges.\n" +
                        "10. Heat oil and fry on low flame until golden and crispy.\n" +
                        "11. Serve hot with chutney.",
                "https://www.youtube.com/watch?v=HCyalu9KMIs"));

// 2️⃣ Pakora
        recipeList.add(new Recipe(
                "S2","Pakora","Snacks",R.drawable.onion_pakora,
                "1 cup gram flour (besan)\n" +
                        "1 onion (thinly sliced)\n" +
                        "2 green chilies (chopped)\n" +
                        "1/2 tsp turmeric powder\n" +
                        "1 tsp red chili powder\n" +
                        "Salt to taste\n" +
                        "Water as needed\n" +
                        "Oil for frying",

                "1. Take gram flour in a bowl.\n" +
                        "2. Add onion, green chilies, turmeric, chili powder and salt.\n" +
                        "3. Add little water and mix to make thick batter.\n" +
                        "4. Heat oil in a deep pan.\n" +
                        "5. Drop small portions of batter into hot oil.\n" +
                        "6. Fry on medium flame until golden brown.\n" +
                        "7. Remove and drain excess oil.\n" +
                        "8. Serve hot with green chutney or ketchup.",
                "https://www.youtube.com/watch?v=7B5KEtf37ec"));

// 3️⃣ Bread Pakora
        recipeList.add(new Recipe(
                "S3","Bread Pakora","Snacks",R.drawable.bread_pakoda,
                "4 bread slices\n" +
                        "2 boiled potatoes (mashed)\n" +
                        "1 onion finely chopped\n" +
                        "1 green chili chopped\n" +
                        "1/2 tsp turmeric powder\n" +
                        "1 tsp red chili powder\n" +
                        "Salt to taste\n" +
                        "1 cup gram flour (besan)\n" +
                        "Water to make batter\n" +
                        "Oil for deep frying",

                "1. Mash boiled potatoes in a bowl.\n" +
                        "2. Add onion, green chili, turmeric, chili powder and salt.\n" +
                        "3. Mix well to prepare stuffing.\n" +
                        "4. Apply stuffing between two bread slices and cut into halves.\n" +
                        "5. Prepare thick batter using gram flour, salt and water.\n" +
                        "6. Heat oil in a deep pan.\n" +
                        "7. Dip stuffed bread into batter completely.\n" +
                        "8. Fry in hot oil on medium flame.\n" +
                        "9. Turn and cook till golden brown and crispy.\n" +
                        "10. Remove and drain excess oil.\n" +
                        "11. Serve hot with chutney or sauce.",
                "https://www.youtube.com/watch?v=_QOatz8kB34"));


// 5️⃣ Pav Bhaji
        recipeList.add(new Recipe(
                "S5","Pav Bhaji","Snacks",R.drawable.pav_bhaji,
                "2 potatoes\n" +
                        "1 cup mixed vegetables (peas, carrot, capsicum)\n" +
                        "2 tomatoes\n" +
                        "1 onion\n" +
                        "2 tbsp butter\n" +
                        "1 tbsp pav bhaji masala\n" +
                        "Salt\n" +
                        "Pav bread",

                "1. Boil potatoes and vegetables until soft.\n" +
                        "2. Mash them properly.\n" +
                        "3. Heat butter in a pan.\n" +
                        "4. Add chopped onions and sauté.\n" +
                        "5. Add tomatoes and cook till soft.\n" +
                        "6. Add pav bhaji masala and salt.\n" +
                        "7. Add mashed vegetables and mix well.\n" +
                        "8. Add water to adjust consistency.\n" +
                        "9. Cook for 10 minutes.\n" +
                        "10. Toast pav with butter.\n" +
                        "11. Serve hot with lemon and onion.",
                "https://www.youtube.com/watch?v=Gbuse4WX01I"));



// 1️⃣1️⃣ Dhokla
        recipeList.add(new Recipe(
                "s11","Dhokla","Snacks",R.drawable.dhokla,
                "1 cup gram flour (besan)\n" +
                        "1 tsp lemon juice\n" +
                        "1 tsp sugar\n" +
                        "Salt to taste\n" +
                        "1 tsp eno fruit salt\n" +
                        "Water\n" +
                        "Mustard seeds\n" +
                        "Curry leaves",

                "1. Take gram flour in a bowl.\n" +
                        "2. Add salt, sugar, lemon juice and water to make smooth batter.\n" +
                        "3. Add eno and mix gently.\n" +
                        "4. Pour batter into greased tray.\n" +
                        "5. Steam for 15 minutes.\n" +
                        "6. Cool and cut into pieces.\n" +
                        "7. Heat oil, add mustard seeds and curry leaves.\n" +
                        "8. Pour tempering over dhokla.\n" +
                        "9. Serve soft and spongy dhokla.",
                "https://www.youtube.com/watch?v=TEGPt00TQxM"));


// 1️⃣2️⃣ Kachori

        recipeList.add(new Recipe(
                "s12","Kachori","Snacks",R.drawable.kachori,
                "2 cups flour\n" +
                        "1/2 cup moong dal (soaked)\n" +
                        "Spices\n" +
                        "Salt\n" +
                        "Oil",

                "1. Prepare dough using flour and oil.\n" +
                        "2. Grind soaked dal coarsely.\n" +
                        "3. Cook dal with spices.\n" +
                        "4. Make small balls from dough.\n" +
                        "5. Stuff dal mixture inside.\n" +
                        "6. Flatten gently.\n" +
                        "7. Fry on low flame till crispy.\n" +
                        "8. Serve hot with chutney.",
                "https://www.youtube.com/watch?v=8FCMAO1_O7o"));


        //Dessert
        // 1️⃣ Chocolate Cake
        recipeList.add(new Recipe(
                "D1","Chocolate Cake","Dessert",R.drawable.chocolate_cake,
                "1 cup flour\n" +
                        "1/2 cup cocoa powder\n" +
                        "1 cup sugar\n" +
                        "2 eggs\n" +
                        "1/2 cup butter\n" +
                        "1 tsp baking powder\n" +
                        "1/2 cup milk",

                "1. Preheat oven to 180°C.\n" +
                        "2. Mix flour, cocoa powder, and baking powder.\n" +
                        "3. In another bowl, beat butter and sugar.\n" +
                        "4. Add eggs and mix well.\n" +
                        "5. Combine dry and wet ingredients.\n" +
                        "6. Add milk and mix to smooth batter.\n" +
                        "7. Pour into baking tin.\n" +
                        "8. Bake for 30–35 minutes.\n" +
                        "9. Cool and serve.",
                "https://www.youtube.com/watch?v=TbZ4D4qDDYE"
        ));

// 2️⃣ Gulab Jamun
        recipeList.add(new Recipe(
                "D2","Gulab Jamun","Dessert",R.drawable.gulab_jamun,
                "1 cup milk powder\n" +
                        "2 tbsp flour\n" +
                        "1/4 tsp baking soda\n" +
                        "Milk (as needed)\n" +
                        "Sugar syrup\n" +
                        "Oil",

                "1. Mix milk powder, flour, and baking soda.\n" +
                        "2. Add milk and make soft dough.\n" +
                        "3. Make small balls.\n" +
                        "4. Heat oil and fry on low flame.\n" +
                        "5. Fry until golden brown.\n" +
                        "6. Soak in warm sugar syrup.\n" +
                        "7. Rest for 1 hour.\n" +
                        "8. Serve warm.",
                "https://www.youtube.com/watch?v=UEm3X99wY0s"
        ));

// 3️⃣ Ice Cream
        recipeList.add(new Recipe(
                "D3","Vanilla Ice Cream","Dessert",R.drawable.ice_cream,
                "2 cups milk\n" +
                        "1 cup cream\n" +
                        "1/2 cup sugar\n" +
                        "1 tsp vanilla essence",

                "1. Heat milk and sugar until dissolved.\n" +
                        "2. Cool the mixture.\n" +
                        "3. Add cream and vanilla essence.\n" +
                        "4. Mix well.\n" +
                        "5. Pour into container.\n" +
                        "6. Freeze for 6–8 hours.\n" +
                        "7. Stir once in between for smooth texture.\n" +
                        "8. Serve chilled.",
                "https://www.youtube.com/watch?v=ZPrd1BUgg_Q"
        ));

// 4️⃣ Brownie
        recipeList.add(new Recipe(
                "D4","Chocolate Brownie","Dessert",R.drawable.brownie,
                "1 cup chocolate\n" +
                        "1/2 cup butter\n" +
                        "1 cup sugar\n" +
                        "2 eggs\n" +
                        "1/2 cup flour",

                "1. Melt chocolate and butter.\n" +
                        "2. Add sugar and mix.\n" +
                        "3. Add eggs and whisk.\n" +
                        "4. Add flour and mix well.\n" +
                        "5. Pour into baking tray.\n" +
                        "6. Bake at 180°C for 25 minutes.\n" +
                        "7. Cool and cut pieces.\n" +
                        "8. Serve.",
                "https://www.youtube.com/watch?v=PBdA7jEPMio"
        ));


// 6️⃣ Rasgulla
        recipeList.add(new Recipe(
                "D6","Rasgulla","Dessert",R.drawable.rasgulla,
                "1 liter milk\n" +
                        "2 tbsp lemon juice\n" +
                        "1 cup sugar\n" +
                        "4 cups water",

                "1. Boil milk and add lemon juice.\n" +
                        "2. Separate paneer.\n" +
                        "3. Knead paneer to smooth dough.\n" +
                        "4. Make small balls.\n" +
                        "5. Prepare sugar syrup.\n" +
                        "6. Boil balls in syrup for 15 minutes.\n" +
                        "7. Cool and serve.",
                "https://www.youtube.com/watch?v=dd9_YZdQS0c"
        ));

// 7️⃣ Ladoo
        recipeList.add(new Recipe(
                "D7","Besan Ladoo","Dessert",R.drawable.ladoo,
                "1 cup besan\n" +
                        "1/2 cup ghee\n" +
                        "3/4 cup sugar\n" +
                        "Cardamom",

                "1. Heat ghee in pan.\n" +
                        "2. Add besan and roast.\n" +
                        "3. Cook till golden and aromatic.\n" +
                        "4. Cool slightly.\n" +
                        "5. Add sugar and cardamom.\n" +
                        "6. Mix well.\n" +
                        "7. Shape into ladoos.\n" +
                        "8. Serve.",
                "https://www.youtube.com/watch?v=QtwGA3l_Jc0"
        ));



// 9️⃣ Barfi
        recipeList.add(new Recipe(
                "D9","Milk Barfi","Dessert",R.drawable.barfi,
                "2 cups milk powder\n" +
                        "1 cup sugar\n" +
                        "1/2 cup milk\n" +
                        "Ghee",

                "1. Heat ghee in pan.\n" +
                        "2. Add milk and sugar.\n" +
                        "3. Stir continuously.\n" +
                        "4. Add milk powder.\n" +
                        "5. Cook till thick.\n" +
                        "6. Spread on tray.\n" +
                        "7. Cool and cut pieces.\n" +
                        "8. Serve.",
                "https://www.youtube.com/watch?v=1q3qTts_lm0&t=159s"
        ));

        //Beverages

        // 1️⃣ Mango Shake
        recipeList.add(new Recipe(
                "Bv1","Mango Shake","Beverages",R.drawable.mango_shake,
                "1 ripe mango (chopped)\n" +
                        "1 cup milk\n" +
                        "2 tbsp sugar\n" +
                        "Ice cubes",

                "1. Peel and chop the mango.\n" +
                        "2. Add mango pieces to blender.\n" +
                        "3. Add milk and sugar.\n" +
                        "4. Blend until smooth.\n" +
                        "5. Add ice cubes and blend again.\n" +
                        "6. Pour into glass.\n" +
                        "7. Serve chilled.",
                "https://www.youtube.com/watch?v=zcNmQX1KFVs"
        ));

// 2️⃣ Banana Shake
        recipeList.add(new Recipe(
                "Bv2","Banana Shake","Beverages",R.drawable.banana_shake,
                "2 bananas\n" +
                        "1 cup milk\n" +
                        "2 tbsp sugar\n" +
                        "Ice cubes",

                "1. Peel bananas and slice.\n" +
                        "2. Add to blender.\n" +
                        "3. Add milk and sugar.\n" +
                        "4. Blend until creamy.\n" +
                        "5. Add ice cubes.\n" +
                        "6. Blend again.\n" +
                        "7. Serve chilled.",
                "https://www.youtube.com/watch?v=eVmS_z36Fzw"
        ));

// 3️⃣ Cold Coffee
        recipeList.add(new Recipe(
                "Bv3","Cold Coffee","Beverages",R.drawable.cold_coffee,
                "1 cup milk\n" +
                        "1 tsp coffee powder\n" +
                        "2 tbsp sugar\n" +
                        "Ice cubes",

                "1. Add milk to blender.\n" +
                        "2. Add coffee powder and sugar.\n" +
                        "3. Blend well.\n" +
                        "4. Add ice cubes.\n" +
                        "5. Blend again till frothy.\n" +
                        "6. Pour into glass.\n" +
                        "7. Serve cold.",
                "https://www.youtube.com/watch?v=PbKmZwniSF4"
        ));


// 9️⃣ Orange Juice
        recipeList.add(new Recipe(
                "Bv9","Orange Juice","Beverages",R.drawable.orange_juice,
                "3 oranges\n" +
                        "Sugar (optional)\n" +
                        "Ice cubes",

                "1. Peel oranges.\n" +
                        "2. Extract juice.\n" +
                        "3. Add sugar if needed.\n" +
                        "4. Mix well.\n" +
                        "5. Add ice cubes.\n" +
                        "6. Serve fresh.",
                "https://www.youtube.com/watch?v=xERYcYBoL8c"
        ));

// 🔟 Watermelon Juice
        recipeList.add(new Recipe(
                "Bv10","Watermelon Juice","Beverages",R.drawable.watermelon_juice,
                "2 cups watermelon\n" +
                        "Sugar\n" +
                        "Ice cubes",

                "1. Cut watermelon into pieces.\n" +
                        "2. Remove seeds.\n" +
                        "3. Add to blender.\n" +
                        "4. Blend until smooth.\n" +
                        "5. Strain if needed.\n" +
                        "6. Add sugar.\n" +
                        "7. Serve chilled.",
                "https://www.youtube.com/watch?v=rAdQ2fHa2BU"
        ));

// 11️⃣ Pineapple Juice
        recipeList.add(new Recipe(
                "Bv11","Pineapple Juice","Beverages",R.drawable.pineapple_juice,
                "2 cups pineapple pieces\n" +
                        "Sugar\n" +
                        "Water",

                "1. Chop pineapple.\n" +
                        "2. Add to blender.\n" +
                        "3. Add water and sugar.\n" +
                        "4. Blend well.\n" +
                        "5. Strain juice.\n" +
                        "6. Serve chilled.",
                "https://www.youtube.com/watch?v=GpJ5hZwFpDM"
        ));


        // 1️⃣ Veg Fried Rice
        recipeList.add(new Recipe(
                "V1","Veg Fried Rice","Vegetarian",R.drawable.veg_fried_rice,
                "1 cup cooked rice\n" +
                        "Mixed vegetables (carrot, beans, peas)\n" +
                        "2 tbsp oil\n" +
                        "1 tsp soy sauce\n" +
                        "Salt\n" +
                        "Pepper",

                "1. Heat oil in a pan.\n" +
                        "2. Add chopped vegetables.\n" +
                        "3. Stir fry for 3–4 minutes.\n" +
                        "4. Add cooked rice.\n" +
                        "5. Add soy sauce, salt and pepper.\n" +
                        "6. Mix well.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=suXQ2mPfhSg"
        ));

// 2️⃣ Paneer Butter Masala
        recipeList.add(new Recipe(
                "V2","Paneer Butter Masala","Vegetarian",R.drawable.paneer_butter_masala,
                "200g paneer cubes\n" +
                        "2 tomatoes\n" +
                        "1 onion\n" +
                        "2 tbsp butter\n" +
                        "Cream\n" +
                        "Spices",

                "1. Heat butter in pan.\n" +
                        "2. Add chopped onion and sauté.\n" +
                        "3. Add tomato puree.\n" +
                        "4. Add spices and cook well.\n" +
                        "5. Add paneer cubes.\n" +
                        "6. Add cream and mix.\n" +
                        "7. Cook for 5 minutes and serve.",
                "https://www.youtube.com/watch?v=bUounn_Bmy4"
        ));

// 3️⃣ Aloo Gobi
        recipeList.add(new Recipe(
                "V3","Aloo Gobi","Vegetarian",R.drawable.aloo_gobi,
                "2 potatoes\n" +
                        "1 cauliflower\n" +
                        "2 tbsp oil\n" +
                        "Spices\n" +
                        "Salt",

                "1. Chop potatoes and cauliflower.\n" +
                        "2. Heat oil in pan.\n" +
                        "3. Add vegetables.\n" +
                        "4. Add spices and salt.\n" +
                        "5. Mix well.\n" +
                        "6. Cover and cook till soft.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=fNdmxfdNkQc"
        ));

// 4️⃣ Veg Pulao
        recipeList.add(new Recipe(
                "V4","Veg Pulao","Vegetarian",R.drawable.veg_pulao,
                "1 cup rice\n" +
                        "Mixed vegetables\n" +
                        "Whole spices\n" +
                        "2 tbsp oil\n" +
                        "Salt",

                "1. Heat oil in cooker.\n" +
                        "2. Add whole spices.\n" +
                        "3. Add vegetables.\n" +
                        "4. Add rice and water.\n" +
                        "5. Add salt.\n" +
                        "6. Cook for 2 whistles.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=nPi2GD2SqfQ&t=310s"
        ));

// 5️⃣ Chole Masala
        recipeList.add(new Recipe(
                "V5","Chole Masala","Vegetarian",R.drawable.chole,
                "1 cup boiled chickpeas\n" +
                        "2 onions\n" +
                        "2 tomatoes\n" +
                        "Spices\n" +
                        "Oil",

                "1. Heat oil in pan.\n" +
                        "2. Add chopped onion.\n" +
                        "3. Add tomato puree.\n" +
                        "4. Add spices.\n" +
                        "5. Add chickpeas.\n" +
                        "6. Cook for 10 minutes.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=RXZsDHKlVGw"
        ));


// 7️⃣ Palak Paneer
        recipeList.add(new Recipe(
                "V7","Palak Paneer","Vegetarian",R.drawable.palak_paneer,
                "200g paneer\n" +
                        "Spinach leaves\n" +
                        "1 onion\n" +
                        "Spices\n" +
                        "Oil",

                "1. Boil spinach and grind.\n" +
                        "2. Heat oil in pan.\n" +
                        "3. Add onion and sauté.\n" +
                        "4. Add spinach puree.\n" +
                        "5. Add spices.\n" +
                        "6. Add paneer cubes.\n" +
                        "7. Cook and serve.",
                "https://www.youtube.com/watch?v=5o-tS6zlp1k"
        ));



// 11️⃣ Vegetable Curry
        recipeList.add(new Recipe(
                "V11","Vegetable Curry","Vegetarian",R.drawable.veg_curry,
                "Mixed vegetables\n" +
                        "Onion\n" +
                        "Tomato\n" +
                        "Spices\n" +
                        "Oil",

                "1. Heat oil.\n" +
                        "2. Add onion.\n" +
                        "3. Add tomato.\n" +
                        "4. Add vegetables.\n" +
                        "5. Add spices.\n" +
                        "6. Cook well.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=0VaMXMwEndU"
        ));

// 12️⃣ Paneer Tikka
        recipeList.add(new Recipe(
                "V12","Paneer Tikka","Vegetarian",R.drawable.paneer_tikka,
                "Paneer cubes\n" +
                        "Curd\n" +
                        "Spices\n" +
                        "Capsicum\n" +
                        "Onion",

                "1. Marinate paneer.\n" +
                        "2. Add vegetables.\n" +
                        "3. Skewer pieces.\n" +
                        "4. Grill or roast.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=BwkX8IeJsik"
        ));

// 13️⃣ Veg Noodles
        recipeList.add(new Recipe(
                "V13","Veg Noodles","Vegetarian",R.drawable.noodles,
                "Noodles\n" +
                        "Vegetables\n" +
                        "Soy sauce\n" +
                        "Oil\n" +
                        "Salt",

                "1. Boil noodles.\n" +
                        "2. Heat oil.\n" +
                        "3. Add vegetables.\n" +
                        "4. Add noodles.\n" +
                        "5. Add sauce.\n" +
                        "6. Mix well.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=j5o7RUtyaRw"
        ));


        // 1️⃣ Chicken Curry
        recipeList.add(new Recipe(
                "NV1","Chicken Curry","NonVeg",R.drawable.chicken_curry,
                "500g chicken\n" +
                        "2 onions\n" +
                        "2 tomatoes\n" +
                        "Spices\n" +
                        "Oil",

                "1. Heat oil in pan.\n" +
                        "2. Add chopped onions and sauté.\n" +
                        "3. Add tomato puree.\n" +
                        "4. Add spices and cook well.\n" +
                        "5. Add chicken pieces.\n" +
                        "6. Cook until tender.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=pAjrJna1TQE"
        ));

// 2️⃣ Egg Curry
        recipeList.add(new Recipe(
                "NV2","Egg Curry","NonVeg",R.drawable.egg_curry,
                "4 boiled eggs\n" +
                        "2 onions\n" +
                        "2 tomatoes\n" +
                        "Spices\n" +
                        "Oil",

                "1. Heat oil in pan.\n" +
                        "2. Add chopped onions.\n" +
                        "3. Add tomato puree.\n" +
                        "4. Add spices.\n" +
                        "5. Add boiled eggs.\n" +
                        "6. Cook for 5–7 minutes.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=_BA9OiyjReQ"
        ));

// 3️⃣ Chicken Biryani
        recipeList.add(new Recipe(
                "NV3","Chicken Biryani","NonVeg",R.drawable.chicken_biryani,
                "2 cups rice\n" +
                        "500g chicken\n" +
                        "Curd\n" +
                        "Spices\n" +
                        "Oil",

                "1. Marinate chicken with curd and spices.\n" +
                        "2. Cook rice separately.\n" +
                        "3. Cook chicken in pan.\n" +
                        "4. Layer rice and chicken.\n" +
                        "5. Cook on low flame.\n" +
                        "6. Mix gently.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=uygb9O-MDPw"
        ));

// 4️⃣ Chicken Fry
        recipeList.add(new Recipe(
                "NV4","Chicken Fry","NonVeg",R.drawable.chicken_fry,
                "500g chicken\n" +
                        "Spices\n" +
                        "Ginger garlic paste\n" +
                        "Oil",

                "1. Marinate chicken with spices.\n" +
                        "2. Heat oil in pan.\n" +
                        "3. Add chicken pieces.\n" +
                        "4. Fry on medium flame.\n" +
                        "5. Cook till golden brown.\n" +
                        "6. Serve hot.",
                "https://www.youtube.com/shorts/j0jdjGI0dHU"
        ));

// 5️⃣ Fish Fry
        recipeList.add(new Recipe(
                "NV5","Fish Fry","NonVeg",R.drawable.fish_fry,
                "Fish pieces\n" +
                        "Spices\n" +
                        "Lemon juice\n" +
                        "Oil",

                "1. Marinate fish with spices and lemon.\n" +
                        "2. Heat oil.\n" +
                        "3. Fry fish pieces.\n" +
                        "4. Cook both sides.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=iSCUt4lIIgI"
        ));



// 7️⃣ Chicken Tikka
        recipeList.add(new Recipe(
                "NV7","Chicken Tikka","NonVeg",R.drawable.chicken_tikka,
                "Chicken cubes\n" +
                        "Curd\n" +
                        "Spices\n" +
                        "Lemon juice",

                "1. Marinate chicken with curd and spices.\n" +
                        "2. Skewer pieces.\n" +
                        "3. Grill or roast.\n" +
                        "4. Cook till done.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=wm5vqZcl8BQ"
        ));



// 9️⃣ Chicken Soup
        recipeList.add(new Recipe(
                "NV9","Chicken Soup","NonVeg",R.drawable.chicken_soup,
                "Chicken pieces\n" +
                        "Vegetables\n" +
                        "Salt\n" +
                        "Pepper\n" +
                        "Water",

                "1. Boil chicken in water.\n" +
                        "2. Add vegetables.\n" +
                        "3. Add salt and pepper.\n" +
                        "4. Cook well.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=6z3ZBAfo0pY"
        ));



// 10 Egg Fried Rice
        recipeList.add(new Recipe(
                "NV12","Egg Fried Rice","NonVeg",R.drawable.egg_fried_rice,
                "Cooked rice\n" +
                        "Eggs\n" +
                        "Vegetables\n" +
                        "Soy sauce\n" +
                        "Oil",

                "1. Heat oil in pan.\n" +
                        "2. Scramble eggs.\n" +
                        "3. Add vegetables.\n" +
                        "4. Add rice.\n" +
                        "5. Add soy sauce.\n" +
                        "6. Mix well.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=biwhzSbRVag"
        ));

// 2️⃣ Fruit Salad
        recipeList.add(new Recipe(
                "H2","Fruit Salad","Healthy",R.drawable.fruit_salad,
                "Apple\n" +
                        "Banana\n" +
                        "Orange\n" +
                        "Grapes\n" +
                        "Honey",

                "1. Chop all fruits.\n" +
                        "2. Take in bowl.\n" +
                        "3. Add honey.\n" +
                        "4. Mix gently.\n" +
                        "5. Chill for 10 minutes.\n" +
                        "6. Serve fresh.",
                "https://www.youtube.com/shorts/Akn4d8Met78"
        ));

// 3️⃣ Vegetable Soup
        recipeList.add(new Recipe(
                "H3","Vegetable Soup","Healthy",R.drawable.veg_soup,
                "Mixed vegetables\n" +
                        "Water\n" +
                        "Salt\n" +
                        "Pepper",

                "1. Boil vegetables in water.\n" +
                        "2. Add salt and pepper.\n" +
                        "3. Cook for 10 minutes.\n" +
                        "4. Blend if needed.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=3Hm6lHBkFUE"
        ));



// 5️⃣ Grilled Paneer
        recipeList.add(new Recipe(
                "H5","Grilled Paneer","Healthy",R.drawable.grilled_paneer,
                "Paneer cubes\n" +
                        "Spices\n" +
                        "Curd\n" +
                        "Capsicum\n" +
                        "Onion",

                "1. Marinate paneer with curd and spices.\n" +
                        "2. Add vegetables.\n" +
                        "3. Skewer pieces.\n" +
                        "4. Grill till cooked.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=BwkX8IeJsik&t=68s"
        ));


// 7️⃣ Smoothie
        recipeList.add(new Recipe(
                "H7","Fruit Smoothie","Healthy",R.drawable.smoothie,
                "Banana\n" +
                        "Milk\n" +
                        "Honey\n" +
                        "Ice cubes",

                "1. Add all ingredients to blender.\n" +
                        "2. Blend till smooth.\n" +
                        "3. Pour into glass.\n" +
                        "4. Serve chilled.",
                "https://www.youtube.com/watch?v=wfHADzgF_P4"
        ));


// 11️⃣ Moong Dal Khichdi
        recipeList.add(new Recipe(
                "H11","Moong Dal Khichdi","Healthy",R.drawable.khichdi,
                "Rice\n" +
                        "Moong dal\n" +
                        "Turmeric\n" +
                        "Salt\n" +
                        "Water",

                "1. Wash rice and dal.\n" +
                        "2. Add water and spices.\n" +
                        "3. Cook in cooker.\n" +
                        "4. Mix well.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=2V2n1UK3LLA&t=154s"
        ));


        //Fast Food
// 2️⃣ Burger
        recipeList.add(new Recipe(
                "F2","Burger","FastFood",R.drawable.burger,
                "Burger buns\n" +
                        "Veg patty\n" +
                        "Lettuce\n" +
                        "Tomato\n" +
                        "Cheese\n" +
                        "Sauce",

                "1. Toast burger buns.\n" +
                        "2. Cook veg patty.\n" +
                        "3. Apply sauce on buns.\n" +
                        "4. Place lettuce, tomato and patty.\n" +
                        "5. Add cheese slice.\n" +
                        "6. Cover with top bun.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=2OQlO0KcIV4"
        ));

// 3️⃣ French Fries
        recipeList.add(new Recipe(
                "F3","French Fries","FastFood",R.drawable.fries,
                "Potatoes\n" +
                        "Oil\n" +
                        "Salt",

                "1. Cut potatoes into strips.\n" +
                        "2. Heat oil in pan.\n" +
                        "3. Deep fry potatoes.\n" +
                        "4. Fry till golden crispy.\n" +
                        "5. Remove excess oil.\n" +
                        "6. Sprinkle salt.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=lB8dMNj7JMA"
        ));


// 5️⃣ Noodles
        recipeList.add(new Recipe(
                "F5","Veg Noodles","FastFood",R.drawable.noodles,
                "Noodles\n" +
                        "Vegetables\n" +
                        "Soy sauce\n" +
                        "Oil",

                "1. Boil noodles.\n" +
                        "2. Heat oil in pan.\n" +
                        "3. Add vegetables.\n" +
                        "4. Add boiled noodles.\n" +
                        "5. Add soy sauce.\n" +
                        "6. Mix well.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=4Q12_scB6AY"
        ));

// 6️⃣ Pasta
        recipeList.add(new Recipe(
                "F6","White Sauce Pasta","FastFood",R.drawable.pasta,
                "Pasta\n" +
                        "Milk\n" +
                        "Butter\n" +
                        "Flour\n" +
                        "Cheese",

                "1. Boil pasta.\n" +
                        "2. Melt butter in pan.\n" +
                        "3. Add flour and cook.\n" +
                        "4. Add milk and stir.\n" +
                        "5. Add cheese.\n" +
                        "6. Add pasta and mix.\n" +
                        "7. Serve hot.",
                "https://www.youtube.com/watch?v=UI9Zacwl33Q"
        ));

// 7️⃣ Momos
        recipeList.add(new Recipe(
                "F7","Momos","FastFood",R.drawable.momos,
                "Maida dough\n" +
                        "Vegetable filling\n" +
                        "Salt\n" +
                        "Oil",

                "1. Prepare dough.\n" +
                        "2. Add filling inside.\n" +
                        "3. Shape momos.\n" +
                        "4. Steam for 10 minutes.\n" +
                        "5. Serve hot with chutney.",
                "https://www.youtube.com/watch?v=qA5C6gLaOGw"
        ));

// 8️⃣ Manchurian
        recipeList.add(new Recipe(
                "F8","Veg Manchurian","FastFood",R.drawable.gobi_manchurian,
                "Vegetable balls\n" +
                        "Soy sauce\n" +
                        "Garlic\n" +
                        "Oil",

                "1. Fry vegetable balls.\n" +
                        "2. Heat oil in pan.\n" +
                        "3. Add garlic.\n" +
                        "4. Add sauces.\n" +
                        "5. Add balls and mix.\n" +
                        "6. Serve hot.",
                "https://www.youtube.com/watch?v=6K8ZJG6cQ2U"
        ));



// 🔟 Pav Bhaji
        recipeList.add(new Recipe(
                "F10","Pav Bhaji","FastFood",R.drawable.pav_bhaji,
                "Boiled vegetables\n" +
                        "Pav\n" +
                        "Butter\n" +
                        "Spices",

                "1. Mash vegetables.\n" +
                        "2. Cook with spices.\n" +
                        "3. Add butter.\n" +
                        "4. Toast pav.\n" +
                        "5. Serve hot.",
                "https://www.youtube.com/watch?v=Gbuse4WX01I"
        ));

// 11️⃣ Chole Bhature
        recipeList.add(new Recipe(
                "F11","Chole Bhature","FastFood",R.drawable.chhole_bhature,
                "Chole\n" +
                        "Flour dough\n" +
                        "Oil\n" +
                        "Spices",

                "1. Cook chole with spices.\n" +
                        "2. Roll dough.\n" +
                        "3. Deep fry bhature.\n" +
                        "4. Serve hot with chole.",
                "https://www.youtube.com/watch?v=nvcMnD0tisc"
        ));


// 15️⃣ Tacos
        recipeList.add(new Recipe(
                "F15","Tacos","FastFood",R.drawable.tacos,
                "Taco shells\n" +
                        "Vegetable filling\n" +
                        "Cheese\n" +
                        "Sauce",

                "1. Prepare filling.\n" +
                        "2. Fill taco shells.\n" +
                        "3. Add cheese.\n" +
                        "4. Add sauce.\n" +
                        "5. Serve fresh.",
                "https://www.youtube.com/watch?v=Wb95nUQZLXM"
        ));



        filteredList.clear();
        filteredList.addAll(recipeList);


    }

    @Override
    protected void onDestroy() {
        if (db != null && db.isOpen()) db.close();
        super.onDestroy();
    }
}
