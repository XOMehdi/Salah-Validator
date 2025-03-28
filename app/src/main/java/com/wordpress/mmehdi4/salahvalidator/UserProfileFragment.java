package com.wordpress.mmehdi4.salahvalidator;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserProfileFragment extends Fragment {

    private ProgressBar progressBarPersonalInfo, progressBarSalahAnalytics;
    private TextView txtViewName, txtViewEmail, txtViewAge, txtViewGender;
    private TextView txtViewRecordedPrayers, txtViewValidInvalidRatio, txtViewMostSuccessful, txtViewLeastSuccessful;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize UI elements
        progressBarPersonalInfo = view.findViewById(R.id.progress_bar_personal_info);
        progressBarSalahAnalytics = view.findViewById(R.id.progress_bar_salah_analytics);

        txtViewName = view.findViewById(R.id.txt_view_name);
        txtViewEmail = view.findViewById(R.id.txt_view_email);
        txtViewAge = view.findViewById(R.id.txt_view_age);
        txtViewGender = view.findViewById(R.id.txt_view_gender);
        txtViewRecordedPrayers = view.findViewById(R.id.txt_view_prayers_recorded);
        txtViewValidInvalidRatio = view.findViewById(R.id.txt_view_valid_invalid_ratio);
        txtViewMostSuccessful = view.findViewById(R.id.txt_view_most_successful);
        txtViewLeastSuccessful = view.findViewById(R.id.txt_view_least_successful);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            displayUserInfo(currentUser.getUid());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnLogout = view.findViewById(R.id.btn_logout);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            btnLogout.setOnClickListener(v -> logOut());
        }
    }

    private void logOut() {
        firebaseAuth.signOut();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
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