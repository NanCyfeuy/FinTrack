package com.kelompokganas.financeapp;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "finance.db", null, 4); // Versi dinaikkan ke 4
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, type TEXT, amount REAL, category TEXT, notes TEXT, date TEXT)");
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        
        // Default categories
        String[] defaultCategories = {"Gaji", "Makanan", "Transportasi", "Belanja", "Hiburan", "Lainnya"};
        for (String cat : defaultCategories) {
            ContentValues values = new ContentValues();
            values.put("name", cat);
            db.insert("categories", null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 3) {
            db.execSQL("DROP TABLE IF EXISTS transactions");
            onCreate(db);
        } else if (oldV < 4) {
            db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
            String[] defaultCategories = {"Gaji", "Makanan", "Transportasi", "Belanja", "Hiburan", "Lainnya"};
            for (String cat : defaultCategories) {
                ContentValues values = new ContentValues();
                values.put("name", cat);
                db.insert("categories", null, values);
            }
        }
    }

    public List<String> getAllCategories() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM categories ORDER BY name ASC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insertWithOnConflict("categories", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void updateTransaction(int id, String title, String type, double amount, String category, String notes, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE transactions SET title=?, type=?, amount=?, category=?, notes=?, date=? WHERE id=?",
                new Object[]{title, type, amount, category, notes, date, id});
    }

    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM transactions WHERE id=?", new Object[]{id});
    }
}