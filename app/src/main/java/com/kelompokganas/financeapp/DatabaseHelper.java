package com.kelompokganas.financeapp;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class untuk mengelola database SQLite aplikasi FinTrack.
 * Menangani pembuatan tabel, upgrade skema, serta operasi CRUD dasar.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    
    // Nama file database dan versi saat ini
    private static final String DATABASE_NAME = "finance.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Dipanggil saat database pertama kali dibuat.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Buat tabel transaksi
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, type TEXT, amount REAL, category TEXT, notes TEXT, date TEXT)");
        
        // Buat tabel kategori
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE COLLATE NOCASE)");
        
        // Inisialisasi kategori default
        String[] defaultCategories = {"Gaji", "Makanan", "Transportasi", "Belanja", "Hiburan", "Lainnya"};
        for (String cat : defaultCategories) {
            ContentValues values = new ContentValues();
            values.put("name", cat);
            db.insert("categories", null, values);
        }
    }

    /**
     * Menangani migrasi database saat versi ditingkatkan.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 3) {
            db.execSQL("DROP TABLE IF EXISTS transactions");
            onCreate(db);
        } else if (oldV < 4) {
            // Migrasi khusus versi 4: Tambahkan tabel kategori jika belum ada
            db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
            String[] defaultCategories = {"Gaji", "Makanan", "Transportasi", "Belanja", "Hiburan", "Lainnya"};
            for (String cat : defaultCategories) {
                ContentValues values = new ContentValues();
                values.put("name", cat);
                db.insert("categories", null, values);
            }
        }
    }

    /**
     * Mengambil semua nama kategori yang tersimpan di database.
     * @return List berisi nama-nama kategori.
     */
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

    /**
     * Menambah kategori baru ke dalam database.
     * @param name Nama kategori baru.
     */
    public void addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        // Gunakan CONFLICT_IGNORE untuk menghindari duplikasi kategori
        db.insertWithOnConflict("categories", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Memperbarui data transaksi yang sudah ada.
     */
    public void updateTransaction(int id, String title, String type, double amount, String category, String notes, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE transactions SET title=?, type=?, amount=?, category=?, notes=?, date=? WHERE id=?",
                new Object[]{title, type, amount, category, notes, date, id});
    }

    /**
     * Menghapus transaksi berdasarkan ID.
     */
    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM transactions WHERE id=?", new Object[]{id});
    }
}
