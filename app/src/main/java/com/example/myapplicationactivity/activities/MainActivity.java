package com.example.myapplicationactivity.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback; // <-- IMPORT BARU
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplicationactivity.R;
import com.example.myapplicationactivity.fragments.HistoryFragment;
import com.example.myapplicationactivity.fragments.HomeFragment;
import com.example.myapplicationactivity.fragments.InfoFragment;
import com.example.myapplicationactivity.fragments.ProfileFragment;
import com.example.myapplicationactivity.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_info) {
                selectedFragment = new InfoFragment();
            }

            View iconView = bottomNavigationView.findViewById(itemId);
            // Cek untuk menghindari crash jika view bukan ImageView
            if (iconView != null) {
                animateMenuItem(iconView);
            }


            return loadFragment(selectedFragment);
        });

        // =====================================================================
        // ==> PERUBAHAN UTAMA ADA DI SINI <==
        // Menggantikan metode onBackPressed() yang lama dengan dispatcher modern
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Cek jika ada fragment di back stack untuk di-pop
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    // Jika tidak ada, biarkan sistem menangani tombol kembali
                    // (misalnya, keluar dari aplikasi).
                    // Nonaktifkan callback ini agar tidak terjadi loop tak terbatas.
                    setEnabled(false);
                    MainActivity.super.onBackPressed(); // Gunakan super.onBackPressed() untuk perilaku default
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        // =====================================================================

    }

    // Fungsi animasi, diubah sedikit agar lebih aman dengan menerima View
    private void animateMenuItem(@NonNull View iconView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 1.2f, 1f);
        scaleX.setDuration(250);
        scaleY.setDuration(250);
        scaleX.start();
        scaleY.start();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // Metode onBackPressed() YANG LAMA SUDAH DIHAPUS DARI SINI
    // @Override
    // public void onBackPressed() { ... }
}