package com.example.redditclone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.redditclone.fragment.CurateFragment;
import com.example.redditclone.fragment.HomeFragment;
import com.example.redditclone.fragment.PremiumFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MyProfileFragment.ProfileUpdateListener {

    public static final int MY_REQUEST_CODE = 10;
    private DrawerLayout mDrawerLayout;
    private ImageView imgavatar;
    private TextView tvname, tvemail;
    private Fragment mActiveFragment;
    private Toolbar mToolbar;

    public static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_CURATE = 1;
    private static final int FRAGMENT_PREMIUM = 2;
    private static final int FRAGMENT_MY_PROFILE = 3;

    private static final int FRAGMENT_LOG_OUT = 4;


    private NavigationView mNavigationView;

    private int mCurrentFragment = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        initUI();
        setupBottomNavigation();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));

        NavigationView navigationViewLeft = findViewById(R.id.nav_view_left);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            switchFragment(FRAGMENT_HOME, new HomeFragment());
            mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        }

        showUserInfomation();
        setupBackStackListener();

        View headerView = mNavigationView.getHeaderView(0);
        View curateItemView = headerView.findViewById(R.id.curate_content_item);

        if (curateItemView != null) {
            curateItemView.setOnClickListener(v -> {
                mDrawerLayout.closeDrawer(GravityCompat.END);
                if (mCurrentFragment != FRAGMENT_CURATE) {
                    replaceFragment(new CurateFragment());
                    mCurrentFragment = FRAGMENT_CURATE;
                }
            });
        }
    }

    private void setupBackStackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (mToolbar != null) {
                if (currentFragment instanceof MyProfileFragment || currentFragment instanceof EditProfileFragment) {
                    mToolbar.setVisibility(View.GONE);
                } else {
                    mToolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView;

    @Override
    public void onProfileUpdated() {
        Log.d("MainActivity", "Profile update received. Refreshing sidebar info.");
        showUserInfomation();
    }

    // THAY ĐỔI: Áp dụng AppExecutors cho hàm showUserInfomation
    public void showUserInfomation() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            // Tác vụ chạy nền: Lấy thông tin người dùng
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }
            final String strName = user.getDisplayName();
            final String strEmail = user.getEmail();
            final Uri photoUrl = user.getPhotoUrl();

            // Gửi kết quả về luồng chính để cập nhật UI
            AppExecutors.getInstance().mainThread().execute(() -> {
                if (strName == null || strName.isEmpty()) {
                    tvname.setText("User");
                } else {
                    tvname.setText("u/" + strName);
                }
                tvemail.setText(strEmail);
                Glide.with(MainActivity.this)
                        .load(photoUrl)
                        .error(R.drawable.profile_default)
                        .into(imgavatar);
            });
        });
    }

    // ... (Toàn bộ các hàm còn lại giữ nguyên như cũ)

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            switchFragment(FRAGMENT_HOME, new HomeFragment());
        } else if (id == R.id.nav_curate) {
            switchFragment(FRAGMENT_CURATE, new CurateFragment());
        } else if (id == R.id.nav_premium) {
            switchFragment(FRAGMENT_PREMIUM, new PremiumFragment());
        } else if (id == R.id.nav_Log_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_my_profile) {
            showProfileFragment();
        }

        mDrawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    private void showProfileFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new MyProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        mCurrentFragment = FRAGMENT_MY_PROFILE;
    }

    public void switchFragment(int fragmentConstant, Fragment newFragment) {
        if (mCurrentFragment == fragmentConstant && getSupportFragmentManager().findFragmentByTag(String.valueOf(fragmentConstant)) != null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment currentActiveFragment = findVisibleFragment(fm);
        if (currentActiveFragment != null) {
            ft.hide(currentActiveFragment);
        }

        Fragment targetFragment = fm.findFragmentByTag(String.valueOf(fragmentConstant));

        if (targetFragment != null) {
            ft.show(targetFragment);
        } else {
            ft.add(R.id.content_frame, newFragment, String.valueOf(fragmentConstant));
        }

        mCurrentFragment = fragmentConstant;
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private Fragment findVisibleFragment(FragmentManager fm) {
        for (Fragment fragment : fm.getFragments()) {
            if (fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

    private void initUI() {
        mNavigationView = findViewById(R.id.nav_view);
        imgavatar = mNavigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tvname = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvemail = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                replaceFragment(new HomeFragment());
                return true;
            }
            else if (itemId == R.id.bottom_answers) {
                replaceFragment(new HomeFragment()); // Temporary
                return true;
            }
            else if (itemId == R.id.bottom_create) {
                // SIMPLE: Just call HomeFragment's method
                openCreatePostDialog();
                return true;
            }
            else if (itemId == R.id.bottom_chat) {
                replaceFragment(new HomeFragment()); // Temporary
                return true;
            }
            else if (itemId == R.id.bottom_inbox) {
                replaceFragment(new HomeFragment()); // Temporary
                return true;
            }

            return false;
        });
    }

    // Simple method to open create post dialog
    private void openCreatePostDialog() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (currentFragment instanceof HomeFragment) {
            // Directly call the method from HomeFragment
            ((HomeFragment) currentFragment).showCreatePostDialog();
        } else {
            // If not in HomeFragment, switch to it first
            replaceFragment(new HomeFragment());
            // Show message to user
            Toast.makeText(this, "Switched to Home - tap create again to post", Toast.LENGTH_SHORT).show();
        }
    }


}
