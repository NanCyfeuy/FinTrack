package com.kelompokganas.financeapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment utama yang menampilkan ringkasan saldo, pemasukan, pengeluaran,
 * dan daftar riwayat transaksi terbaru.
 */
public class DashboardFragment extends Fragment {
    private RecyclerView rv;
    private DatabaseHelper dbHelper;
    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense;
    private FloatingActionButton btnGoToAdd;
    private DecimalFormat df = new DecimalFormat("#,###"); // Format angka ribuan

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout dashboard
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Inisialisasi komponen UI
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        rv = view.findViewById(R.id.rvTransactions);
        btnGoToAdd = view.findViewById(R.id.btnGoToAdd);
        dbHelper = new DatabaseHelper(getContext());

        // Memuat data dari database
        loadData();

        // Navigasi ke fragment tambah transaksi
        btnGoToAdd.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, new AddTransactionFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    /**
     * Mengambil data transaksi dari database, menghitung total,
     * dan menampilkannya ke RecyclerView.
     */
    private void loadData() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Ambil semua transaksi diurutkan dari yang terbaru (ID terbesar)
        Cursor cursor = db.rawQuery("SELECT * FROM transactions ORDER BY id DESC", null);

        double balance = 0;
        double income = 0;
        double expense = 0;

        if (cursor.moveToFirst()) {
            do {
                // Ekstraksi data dari cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                double amt = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                // Tambahkan ke list untuk adapter
                list.add(new Transaction(id, title, type, amt, category, notes, date));

                // Kalkulasi total berdasarkan tipe
                if ("Pemasukan".equals(type)) {
                    income += amt;
                    balance += amt;
                } else {
                    expense += amt;
                    balance -= amt;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Tampilkan hasil kalkulasi dengan format rupiah
        tvTotalBalance.setText("Rp " + df.format(balance).replace(',', '.'));
        tvTotalIncome.setText("Rp " + df.format(income).replace(',', '.'));
        tvTotalExpense.setText("Rp " + df.format(expense).replace(',', '.'));

        // Setup RecyclerView dan Adapter
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new TransactionAdapter(list, transaction -> {
            // Callback ketika item diklik: Buka Fragment AddTransaction dalam mode edit
            AddTransactionFragment fragment = new AddTransactionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", transaction.getId());
            bundle.putString("title", transaction.getTitle());
            bundle.putString("type", transaction.getType());
            bundle.putDouble("amount", transaction.getAmount());
            bundle.putString("category", transaction.getCategory());
            bundle.putString("notes", transaction.getNotes());
            bundle.putString("date", transaction.getDate());
            fragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }));
    }
}
