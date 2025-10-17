package com.example.redditclone;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.redditclone.fragment.CurateFragment;
import com.example.redditclone.fragment.HomeFragment;
import com.example.redditclone.fragment.MyProfileFragment;
import com.example.redditclone.fragment.PremiumFragment;
import com.example.redditclone.fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MY_REQUEST_CODE = 10;
    private DrawerLayout mDrawerLayout;
    private ImageView imgavatar;
    private TextView tvname, tvemail;
    private final MyProfileFragment mMyProfileFragment = new MyProfileFragment();
    private Fragment mActiveFragment;

    public static final int FRAGMENT_PROFILE = 0;
    private static final int FRAGMENT_CURATE = 1;
    private static final int FRAGMENT_PREMIUM = 2;
    private static final int FRAGMENT_MY_PROFILE = 3;

    private NavigationView mNavigationView;

    private int mCurrentFragment = FRAGMENT_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));

        NavigationView navigationViewLeft = findViewById(R.id.nav_view_left);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        mNavigationView.setNavigationItemSelectedListener(this);

        switchFragment(FRAGMENT_PROFILE, new HomeFragment());
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        showUserInfomation();

        View headerView = mNavigationView.getHeaderView(0);
        View curateItemView = headerView.findViewById(R.id.curate_content_item);

        if (curateItemView != null) {
            curateItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                    if (mCurrentFragment != FRAGMENT_CURATE) {
                        replaceFragment(new CurateFragment());
                        mCurrentFragment = FRAGMENT_CURATE;
                    }
                }
            });
        }


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


    private void initUI() {
        mNavigationView = findViewById(R.id.nav_view);
        imgavatar = mNavigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tvname = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvemail = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_curate) {
            switchFragment(FRAGMENT_CURATE, new CurateFragment());
        } else if (id == R.id.nav_premium) {
            switchFragment(FRAGMENT_PREMIUM, new PremiumFragment());
        } else if (id == R.id.nav_Log_out) {
            // ... (Logic Log out)
        } else if (id == R.id.nav_my_profile) {
            switchFragment(FRAGMENT_MY_PROFILE, mMyProfileFragment);
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

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (fragment instanceof CurateFragment) {
            if (toolbar != null) {
                toolbar.setVisibility(View.GONE);
            }
        } else {
            if (toolbar != null) {
                toolbar.setVisibility(View.VISIBLE);
            }
        }
    }
    public void showUserInfomation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            return;
        }
        String strName = user.getDisplayName();
        String strEmail = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        if (strName == null) {
            tvname.setVisibility(View.GONE);
        }else {
            tvname.setVisibility(View.VISIBLE);
        }
        tvname.setText("u/" + strName);
        tvemail.setText(strEmail);
        Glide.with(this).load(photoUrl).error(R.drawable.profile_default).into(imgavatar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

    }

    public void switchFragment(int fragmentConstant, Fragment newFragment) {
        if (mCurrentFragment == fragmentConstant) {
            return; // Đã ở Fragment này rồi, không làm gì cả.
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // 1. Ẩn Fragment đang hoạt động (Active Fragment)
        if (mActiveFragment != null) {
            ft.hide(mActiveFragment);
        }

        // 2. Tìm Fragment mục tiêu (nếu đã tồn tại)
        Fragment targetFragment = fm.findFragmentByTag(String.valueOf(fragmentConstant));

        if (targetFragment != null) {
            // 3. Nếu Fragment đã tồn tại, hiện nó
            ft.show(targetFragment);
            mActiveFragment = targetFragment;
        } else {
            // 4. Nếu Fragment chưa tồn tại, thêm nó vào và gán Tag
            // KHÔNG dùng addToBackStack ở đây cho các Fragment chính
            ft.add(R.id.content_frame, newFragment, String.valueOf(fragmentConstant));
            mActiveFragment = newFragment;
        }

        // Xử lý ẩn/hiện Toolbar (giống logic cũ của bạn)
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (mActiveFragment instanceof CurateFragment) {
            if (toolbar != null) toolbar.setVisibility(View.GONE);
        } else {
            if (toolbar != null) toolbar.setVisibility(View.VISIBLE);
        }

        mCurrentFragment = fragmentConstant;
        ft.commit();
    }
}