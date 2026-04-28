package com.example.studentdata.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentDB.db";


    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "students";

    private static final String COL_ROLL = "roll";
    private static final String COL_NAME = "name";
    private static final String COL_MARKS = "marks";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ROLL + " TEXT PRIMARY KEY, " +
                COL_NAME + " TEXT, " +
                COL_MARKS + " TEXT)";
        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String roll, String name, String marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_ROLL, roll);
        values.put(COL_NAME, name);
        values.put(COL_MARKS, marks);

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updateData(String roll, String name, String marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_MARKS, marks);

        int result = db.update(TABLE_NAME, values, "roll=?", new String[]{roll});

        return result > 0;
    }

    public boolean deleteData(String roll) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "roll=?", new String[]{roll});

        return result > 0;
    }
}
