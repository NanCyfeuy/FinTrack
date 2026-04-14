package com.kelompokganas.financeapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText etAmount, etCategory, etTitle, etNotes;
    private Button btnTypeIncome, btnTypeExpense, btnSave, btnDelete;
    private String selectedType = "Pemasukan";
    private int transactionId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        dbHelper = new DatabaseHelper(getContext());
        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        etCategory = view.findViewById(R.id.etCategory);
        etNotes = view.findViewById(R.id.etNotes);
        btnTypeIncome = view.findViewById(R.id.btnTypeIncome);
        btnTypeExpense = view.findViewById(R.id.btnTypeExpense);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);

        if (getArguments() != null) {
            transactionId = getArguments().getInt("id", -1);
            etTitle.setText(getArguments().getString("title"));
            etAmount.setText(String.valueOf((int)getArguments().getDouble("amount")));
            etCategory.setText(getArguments().getString("category"));
            etNotes.setText(getArguments().getString("notes"));
            selectedType = getArguments().getString("type");
            
            btnSave.setText("UPDATE TRANSAKSI");
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnTypeIncome.setOnClickListener(v -> selectType("Pemasukan"));
        btnTypeExpense.setOnClickListener(v -> selectType("Pengeluaran"));

        // Default state
        selectType(selectedType);

        btnSave.setOnClickListener(v -> saveData());
        btnDelete.setOnClickListener(v -> deleteData());

        return view;
    }

    private void selectType(String type) {
        selectedType = type;
        if (type.equals("Pemasukan")) {
            // Selected Income: Transparan Hijau & Teks Hijau
            btnTypeIncome.setBackgroundResource(R.drawable.bg_btn_type_selected_income);
            btnTypeIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_green));
            
            // Unselected Expense: Putih Keabu-abuan & Teks Hitam
            btnTypeExpense.setBackgroundResource(R.drawable.bg_btn_type_unselected);
            btnTypeExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else {
            // Selected Expense: Transparan Merah & Teks Merah
            btnTypeExpense.setBackgroundResource(R.drawable.bg_btn_type_selected_expense);
            btnTypeExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.expense_red));
            
            // Unselected Income: Putih Keabu-abuan & Teks Hitam
            btnTypeIncome.setBackgroundResource(R.drawable.bg_btn_type_unselected);
            btnTypeIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }
    }

    private void saveData() {
        String title = etTitle.getText().toString();
        String amountStr = etAmount.getText().toString();
        String category = etCategory.getText().toString();
        String notes = etNotes.getText().toString();

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Judul dan Nominal harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (transactionId == -1) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("type", selectedType);
            values.put("amount", amount);
            values.put("category", category);
            values.put("notes", notes);
            values.put("date", currentDate);
            db.insert("transactions", null, values);
            db.close();
            Toast.makeText(getContext(), "Berhasil disimpan", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateTransaction(transactionId, title, selectedType, amount, category, notes, currentDate);
            Toast.makeText(getContext(), "Berhasil diperbarui", Toast.LENGTH_SHORT).show();
        }
        
        getParentFragmentManager().popBackStack();
    }

    private void deleteData() {
        if (transactionId != -1) {
            dbHelper.deleteTransaction(transactionId);
            Toast.makeText(getContext(), "Berhasil dihapus", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }
}