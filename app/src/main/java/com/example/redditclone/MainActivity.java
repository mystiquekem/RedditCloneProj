package com.example.redditclone;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.redditclone.fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private static final int FRAGMENT_PROFILE = 0;
    private static final int FRAGMENT_CURATE = 1;
    private static final int FRAGMENT_PREMIUM = 2;

    private int mCurrentFragment = FRAGMENT_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onStart was called");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity", "onStop was called");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity", "onPause was called");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume was called");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_profile) {
            // Handle the profile action
            if (mCurrentFragment != FRAGMENT_PROFILE) {
                replaceFragment(new ProfileFragment());
                mCurrentFragment = FRAGMENT_PROFILE;
            }
        } else if (id == R.id.nav_curate) {
            // Handle the curate action
            if (mCurrentFragment != FRAGMENT_CURATE) {
                replaceFragment(new ProfileFragment());
                mCurrentFragment = FRAGMENT_CURATE;
            }
        } else if (id == R.id.nav_premium) {
            // Handle the premium action
            if (mCurrentFragment != FRAGMENT_PREMIUM) {
                replaceFragment(new ProfileFragment());
                mCurrentFragment = FRAGMENT_PREMIUM;
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_open_drawer) {
            mDrawerLayout.openDrawer(GravityCompat.END);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
}