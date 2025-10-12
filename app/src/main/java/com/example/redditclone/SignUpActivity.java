package com.example.redditclone;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtpassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initUi();
        initListener();
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edt_email);
        edtpassword = findViewById(R.id.edt_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }


    private void initListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
    }

            private void onClickSignUp() {
                String strEmail = edtEmail.getText().toString().trim();
                String strPassword = edtpassword.getText().toString().trim();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(strEmail, strPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                       Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                       startActivity(intent);
                       finishAffinity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
}   }



