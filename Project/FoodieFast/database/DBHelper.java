package com.example.foodiefast.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

import com.example.foodiefast.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "FoodieFast.db";
    private static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT UNIQUE," +
                "phone TEXT," +
                "password TEXT," +
                "image TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT," +
                "category TEXT," +
                "imageUrl TEXT," +
                "ingredients TEXT," +
                "procedure TEXT," +
                "youtube TEXT)");

        db.execSQL("CREATE TABLE liked (" +
                "recipeId TEXT PRIMARY KEY)");

        db.execSQL("CREATE TABLE saved (" +
                "recipeId TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS liked");
        db.execSQL("DROP TABLE IF EXISTS saved");

        onCreate(db);
    }


    public boolean checkUserExists(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT 1 FROM users WHERE email=?",
                new String[]{email}
        );

        boolean exists = c.moveToFirst();
        c.close();

        return exists;
    }

    public boolean registerUser(String name, String email, String phone, String password) {

        if (checkUserExists(email)) return false;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("phone", phone);
        cv.put("password", password);
        cv.put("image", "");

        long res = db.insert("users", null, cv);

        return res != -1;
    }


    public boolean loginUser(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT 1 FROM users WHERE email=? AND password=?",
                new String[]{email, password}
        );

        boolean exists = c.moveToFirst();
        c.close();

        return exists;
    }


    public String getUserName(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT name FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            String name = c.getString(0);
            c.close();
            return name;
        }

        c.close();
        return "User";
    }

    public String getUserPhone(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT phone FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            String phone = c.getString(0);
            c.close();
            return phone;
        }

        c.close();
        return "";
    }

    public void updateUser(String email, String name, String phone) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);

        db.update("users", cv, "email=?", new String[]{email});
    }

    public void saveProfileImage(String email, String imageUri) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("image", imageUri);

        db.update("users", cv, "email=?", new String[]{email});
    }

    public String getProfileImage(String email) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT image FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            String img = c.getString(0);
            c.close();
            return img;
        }

        c.close();
        return "";
    }


    public void insertRecipe(Recipe r) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("id", r.id);
        cv.put("name", r.name);
        cv.put("category", r.category);
        cv.put("imageUrl", r.imageUrl);
        cv.put("ingredients", r.ingredients);
        cv.put("procedure", r.procedure);
        cv.put("youtube", r.youtubeLink);

        db.insertWithOnConflict("recipes", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public List<Recipe> getAllRecipes() {

        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM recipes", null);

        while (c.moveToNext()) {

            Recipe r = new Recipe();

            r.id = c.getString(0);
            r.name = c.getString(1);
            r.category = c.getString(2);
            r.imageUrl = c.getString(3);
            r.ingredients = c.getString(4);
            r.procedure = c.getString(5);
            r.youtubeLink = c.getString(6);

            list.add(r);
        }

        c.close();
        return list;
    }



    public void likeRecipe(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO liked(recipeId) VALUES(?)", new Object[]{id});
    }

    public void unlikeRecipe(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM liked WHERE recipeId=?", new Object[]{id});
    }

    public boolean isLiked(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT 1 FROM liked WHERE recipeId=?",
                new String[]{id}
        );

        boolean exists = c.moveToFirst();
        c.close();

        return exists;
    }

    public List<String> getLikedIds() {

        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT recipeId FROM liked", null);

        while (c.moveToNext()) {
            list.add(c.getString(0));
        }

        c.close();
        return list;
    }


    public void saveRecipe(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO saved(recipeId) VALUES(?)", new Object[]{id});
    }

    public void unsaveRecipe(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM saved WHERE recipeId=?", new Object[]{id});
    }

    public boolean isSaved(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT 1 FROM saved WHERE recipeId=?",
                new String[]{id}
        );

        boolean exists = c.moveToFirst();
        c.close();

        return exists;
    }

    public List<String> getSavedIds() {

        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT recipeId FROM saved", null);

        while (c.moveToNext()) {
            list.add(c.getString(0));
        }

        c.close();
        return list;
    }
}
