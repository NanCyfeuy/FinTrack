package com.kelompokganas.financeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity utama aplikasi yang berfungsi sebagai kontainer untuk fragment.
 * Mengatur tampilan awal dengan memuat DashboardFragment.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mengatur layout utama dari res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        // Jika activity baru pertama kali dibuat, tampilkan DashboardFragment aphfaif
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new DashboardFragment())
                    .commit();
        }
    }
}
