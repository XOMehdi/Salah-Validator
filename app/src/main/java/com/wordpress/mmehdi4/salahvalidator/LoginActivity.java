package com.wordpress.mmehdi4.salahvalidator;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edtTxtLoginEmail, edtTxtLoginPass;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        edtTxtLoginEmail = findViewById(R.id.edt_txt_login_email);
        edtTxtLoginPass = findViewById(R.id.edt_txt_login_pass);
        CheckBox showLoginPasswordCheckBox = findViewById(R.id.checkbox_login_show_pass);
        Button loginButton = findViewById(R.id.btn_login);
        TextView edtTxtRegister = findViewById(R.id.txt_view_register);

        showLoginPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtTxtLoginPass.setTransformationMethod(null);
            } else {
                edtTxtLoginPass.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        loginButton.setOnClickListener(v -> login());

        edtTxtRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // Check if the user is already logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void login() {
        String email = edtTxtLoginEmail.getText().toString();
        String password = edtTxtLoginPass.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Prevent back navigation
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Failed to Login\nIncorrect Email or Password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

