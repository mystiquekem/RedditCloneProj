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
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.redditclone.fragment.HomeFragment;
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

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent intent = result.getData();
                if (intent == null){
                    return;
                }
                Uri uri = intent.getData();
                mMyProfileFragment.setUri(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mMyProfileFragment.setBitmapImageView(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private static final int FRAGMENT_PROFILE = 0;
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

        NavigationView navigationViewLeft = findViewById(R.id.nav_view_left);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        mNavigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        showUserInfomation();


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
        } else if (id == R.id.nav_Log_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_my_profile) {
            if (mCurrentFragment != FRAGMENT_MY_PROFILE) {
                replaceFragment(mMyProfileFragment);
                mCurrentFragment = FRAGMENT_MY_PROFILE;
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

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
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
            tvname.setText(strName);
            tvemail.setText(strEmail);
            Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(imgavatar);
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
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}