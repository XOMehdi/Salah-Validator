package com.wordpress.mmehdi4.salahvalidator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    private TextView txtViewName, txtViewEmail, txtViewAge, txtViewGender, txtViewRecordedPrayers, txtViewValidInvalidRatio, txtViewMostSuccessful, txtViewLeastSuccessful;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        txtViewName = findViewById(R.id.txt_view_name);
        txtViewEmail = findViewById(R.id.txt_view_email);
        txtViewAge = findViewById(R.id.txt_view_age);
        txtViewGender = findViewById(R.id.txt_view_gender);
        txtViewRecordedPrayers = findViewById(R.id.txt_view_prayers_recorded);
        txtViewValidInvalidRatio = findViewById(R.id.txt_view_valid_invalid_ratio);
        txtViewMostSuccessful = findViewById(R.id.txt_view_most_successful);
        txtViewLeastSuccessful = findViewById(R.id.txt_view_least_successful);

        String userId = getIntent().getStringExtra("user_id");

        displayUserInfo(userId);
    }

    private void displayUserInfo(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        txtViewName.setText(user.getName());
                        txtViewEmail.setText(user.getEmail());
                        txtViewAge.setText(String.valueOf(user.getAge()));
                        txtViewGender.setText(user.getGender());
                        txtViewRecordedPrayers.setText(String.valueOf(user.getTotalRecordedPrayers()));
                        txtViewValidInvalidRatio.setText(user.getValidInvalidRatio());
                        txtViewMostSuccessful.setText(user.getMostSuccessfulSalah());
                        txtViewLeastSuccessful.setText(user.getLeastSuccessfulSalah());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}