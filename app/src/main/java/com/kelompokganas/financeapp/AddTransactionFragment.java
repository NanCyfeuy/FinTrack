package com.kelompokganas.financeapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment untuk menambah atau memperbarui transaksi (Pemasukan/Pengeluaran).
 */
public class AddTransactionFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText etAmount, etTitle, etNotes;
    private Spinner spCategory;
    private ImageButton btnAddCategory;
    private Button btnTypeIncome, btnTypeExpense, btnSave, btnDelete;
    private String selectedType = "Pemasukan"; // Default tipe transaksi
    private int transactionId = -1; // -1 berarti transaksi baru
    private String initialCategory = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        // Inisialisasi Database Helper dan View
        dbHelper = new DatabaseHelper(getContext());
        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        spCategory = view.findViewById(R.id.spCategory);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        etNotes = view.findViewById(R.id.etNotes);
        btnTypeIncome = view.findViewById(R.id.btnTypeIncome);
        btnTypeExpense = view.findViewById(R.id.btnTypeExpense);
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);

        // Memuat kategori ke dalam Spinner
        loadCategories();

        // Cek jika ada data yang dikirim (mode edit)
        if (getArguments() != null) {
            transactionId = getArguments().getInt("id", -1);
            etTitle.setText(getArguments().getString("title"));
            etAmount.setText(String.valueOf((int)getArguments().getDouble("amount")));
            initialCategory = getArguments().getString("category");
            etNotes.setText(getArguments().getString("notes"));
            selectedType = getArguments().getString("type");
            
            btnSave.setText("UPDATE TRANSAKSI");
            btnDelete.setVisibility(View.VISIBLE);
            
            setSpinnerToValue(spCategory, initialCategory);
        }

        // Listener untuk menambah kategori baru
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        // Listener untuk memilih tipe transaksi
        btnTypeIncome.setOnClickListener(v -> selectType("Pemasukan"));
        btnTypeExpense.setOnClickListener(v -> selectType("Pengeluaran"));

        // Set status default tombol tipe
        selectType(selectedType);

        // Listener untuk tombol simpan dan hapus
        btnSave.setOnClickListener(v -> saveData());
        btnDelete.setOnClickListener(v -> deleteData());

        // Menyembunyikan keyboard saat berinteraksi dengan Spinner
        spCategory.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    /**
     * Menyembunyikan keyboard virtual.
     */
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Memuat daftar kategori dari database ke Spinner.
     */
    private void loadCategories() {
        List<String> categories = dbHelper.getAllCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    /**
     * Mengatur posisi Spinner berdasarkan nilai string.
     */
    private void setSpinnerToValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    /**
     * Menampilkan dialog untuk input kategori baru.
     */
    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tambah Kategori Baru");

        final EditText input = new EditText(getContext());
        input.setHint("Nama Kategori");
        builder.setView(input);

        builder.setPositiveButton("Tambah", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                dbHelper.addCategory(categoryName);
                loadCategories();
                setSpinnerToValue(spCategory, categoryName);
            }
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Mengatur UI tombol Pemasukan/Pengeluaran saat dipilih.
     */
    private void selectType(String type) {
        selectedType = type;
        if (type.equals("Pemasukan")) {
            // Hijau untuk Pemasukan
            btnTypeIncome.setBackgroundResource(R.drawable.bg_btn_type_selected_income);
            btnTypeIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_green));
            
            btnTypeExpense.setBackgroundResource(R.drawable.bg_btn_type_unselected);
            btnTypeExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else {
            // Merah untuk Pengeluaran
            btnTypeExpense.setBackgroundResource(R.drawable.bg_btn_type_selected_expense);
            btnTypeExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.expense_red));
            
            btnTypeIncome.setBackgroundResource(R.drawable.bg_btn_type_unselected);
            btnTypeIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }
    }

    /**
     * Menyimpan data transaksi ke database (Insert atau Update).
     */
    private void saveData() {
        String title = etTitle.getText().toString();
        String amountStr = etAmount.getText().toString();
        String category = spCategory.getSelectedItem() != null ? spCategory.getSelectedItem().toString() : "";
        String notes = etNotes.getText().toString();

        // Validasi input
        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Judul dan Nominal harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (transactionId == -1) {
            // Simpan transaksi baru
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
            // Update transaksi yang ada
            dbHelper.updateTransaction(transactionId, title, selectedType, amount, category, notes, currentDate);
            Toast.makeText(getContext(), "Berhasil diperbarui", Toast.LENGTH_SHORT).show();
        }
        
        // Kembali ke layar sebelumnya
        getParentFragmentManager().popBackStack();
    }

    /**
     * Menghapus transaksi dari database.
     */
    private void deleteData() {
        if (transactionId != -1) {
            dbHelper.deleteTransaction(transactionId);
            Toast.makeText(getContext(), "Berhasil dihapus", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }
}
