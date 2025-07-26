package com.example.playmood.view.router;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.playmood.R;
import com.example.playmood.view.home.HomeFragment;
import com.example.playmood.view.user.CameraFragment;
import com.example.playmood.view.user.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RouterActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private final HomeFragment homeFragment = new HomeFragment();
    private final CameraFragment cameraFragment = new CameraFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load fragment pertama (Home)
        loadFragment(homeFragment);

        // Event listener saat item di bottom navigation diklik
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            int id = item.getItemId();
            if (id == R.id.navbar_menu) {
                selectedFragment = homeFragment;
            } else if (id == R.id.navbar_camera) {
                selectedFragment = cameraFragment;
            } else if (id == R.id.navbar_profile) {
                selectedFragment = profileFragment;
            } else {
                return false;
            }

            loadFragment(selectedFragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }
}
