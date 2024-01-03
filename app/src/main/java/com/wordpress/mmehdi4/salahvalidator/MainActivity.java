package com.wordpress.mmehdi4.salahvalidator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtViewFajr = findViewById(R.id.txt_view_fajr_time);
        TextView txtViewSunrise = findViewById(R.id.txt_view_sunrise_time);
        TextView txtViewDhuhr = findViewById(R.id.txt_view_dhuhr_time);
        TextView txtViewAsr = findViewById(R.id.txt_view_asr_time);
        TextView txtViewMaghrib = findViewById(R.id.txt_view_maghrib_time);
        TextView txtViewIsha = findViewById(R.id.txt_view_isha_time);
        Button btnValidateSalah = findViewById(R.id.btn_validate_salah);
        Button btnPersonalInfo = findViewById(R.id.btn_personal_info);
        Button btnLogout = findViewById(R.id.btn_logout);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            new FetchPrayerTimesTask(this, txtViewFajr, txtViewSunrise, txtViewDhuhr, txtViewAsr, txtViewMaghrib, txtViewIsha).execute();

            btnValidateSalah.setOnClickListener(v -> startActivity(new Intent(this, SalahValidator.class)));

            btnPersonalInfo.setOnClickListener(v -> userProfileActivity());

            btnLogout.setOnClickListener(v -> logOut());
        }
    }

    private void userProfileActivity() {
        String userId = currentUser.getUid();
        Intent userProfileIntent = new Intent(MainActivity.this, UserProfile.class);
        userProfileIntent.putExtra("user_id", userId);
        startActivity(userProfileIntent);
    }

    private void logOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }
}
