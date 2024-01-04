package com.wordpress.mmehdi4.salahvalidator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    private ProgressBar progressBarPersonalInfo, progressBarSalahAnalytics;
    private TextView txtViewName, txtViewEmail, txtViewAge, txtViewGender, txtViewRecordedPrayers, txtViewValidInvalidRatio, txtViewMostSuccessful, txtViewLeastSuccessful;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        progressBarPersonalInfo = findViewById(R.id.progress_bar_personal_info);
        progressBarSalahAnalytics = findViewById(R.id.progress_bar_salah_analytics);

        txtViewName = findViewById(R.id.txt_view_name);
        txtViewEmail = findViewById(R.id.txt_view_email);
        txtViewAge = findViewById(R.id.txt_view_age);
        txtViewGender = findViewById(R.id.txt_view_gender);
        txtViewRecordedPrayers = findViewById(R.id.txt_view_prayers_recorded);
        txtViewValidInvalidRatio = findViewById(R.id.txt_view_valid_invalid_ratio);
        txtViewMostSuccessful = findViewById(R.id.txt_view_most_successful);
        txtViewLeastSuccessful = findViewById(R.id.txt_view_least_successful);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        String userId = getIntent().getStringExtra("user_id");

        displayUserInfo(userId);
    }

    private void displayUserInfo(String userId) {
        progressBarPersonalInfo.setVisibility(View.VISIBLE);
        progressBarSalahAnalytics.setVisibility(View.VISIBLE);

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null) {
                        txtViewName.setText(user.getName());
                        txtViewEmail.setText(user.getEmail());
                        txtViewAge.setText(String.valueOf(user.getAge()));
                        txtViewGender.setText(user.getGender());
                        progressBarPersonalInfo.setVisibility(View.GONE);

                        txtViewRecordedPrayers.setText(String.valueOf(user.getTotalRecordedPrayers()));
                        txtViewValidInvalidRatio.setText(user.getValidInvalidRatio());
                        txtViewMostSuccessful.setText(user.getMostSuccessfulSalah());
                        txtViewLeastSuccessful.setText(user.getLeastSuccessfulSalah());
                        progressBarSalahAnalytics.setVisibility(View.GONE);
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