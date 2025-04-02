package com.wordpress.mmehdi4.salahvalidator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtTxtName, edtTxtEmail, edtTxtPass, edtTxtAge;
    private Spinner spinGender;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        edtTxtName = findViewById(R.id.edt_txt_name);
        edtTxtEmail = findViewById(R.id.edt_txt_email);
        edtTxtPass = findViewById(R.id.edt_txt_pass);
        edtTxtAge = findViewById(R.id.edt_txt_age);
        spinGender = findViewById(R.id.spin_gender);
        CheckBox checkboxShowPass = findViewById(R.id.checkbox_show_pass);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView edtTxtLogin = findViewById(R.id.txt_view_login);

        // Define the gender options with "Select Gender" as the first item
        String[] genderOptions = {"Select Gender", "Male", "Female", "Other"};

        // Create an ArrayAdapter using the genderOptions array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        Spinner spinGender = findViewById(R.id.spin_gender);
        spinGender.setAdapter(adapter);

        // Change text color of "Select Gender" and prevent selection as valid input
        spinGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedView = (TextView) parent.getChildAt(0);
                if (selectedView != null) {
                    if (position == 0) {  // First item is "Select Gender"
                        selectedView.setTextColor(Color.GRAY); // Indicate it's a placeholder
                    } else {
                        selectedView.setTextColor(Color.BLACK); // Normal text color for valid selections
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        checkboxShowPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtTxtPass.setTransformationMethod(null);
            } else {
                edtTxtPass.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        btnRegister.setOnClickListener(v -> signUp());

        edtTxtLogin.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

    }
    private void signUp() {
        String name = edtTxtName.getText().toString();
        String email = edtTxtEmail.getText().toString();
        String password = edtTxtPass.getText().toString();
        int age = Integer.parseInt(edtTxtAge.getText().toString());
        String gender = spinGender.getSelectedItem().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                        UserModel user = new UserModel(userId, name, email, age, gender);
                        databaseReference.child(userId).setValue(user);

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevent back navigation
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to Register\nPlease Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
