package com.kelompokganas.financeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "finance.db", null, 3); // Versi dinaikkan ke 3
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, type TEXT, amount REAL, category TEXT, notes TEXT, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 3) {
            db.execSQL("DROP TABLE IF EXISTS transactions");
            onCreate(db);
        }
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