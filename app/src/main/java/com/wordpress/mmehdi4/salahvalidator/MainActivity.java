package com.wordpress.mmehdi4.salahvalidator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private TextView txtViewFajr, txtViewSunrise, txtViewDhuhr, txtViewAsr, txtViewMaghrib, txtViewIsha;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtViewFajr = findViewById(R.id.txt_view_fajr_time);
        txtViewSunrise = findViewById(R.id.txt_view_sunrise_time);
        txtViewDhuhr = findViewById(R.id.txt_view_dhuhr_time);
        txtViewAsr = findViewById(R.id.txt_view_asr_time);
        txtViewMaghrib = findViewById(R.id.txt_view_maghrib_time);
        txtViewIsha = findViewById(R.id.txt_view_isha_time);
        Button btnValidateSalah = findViewById(R.id.btn_validate_salah);
        Button btnPersonalInfo = findViewById(R.id.btn_personal_info);
        Button btnLogout = findViewById(R.id.btn_logout);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            executeFetchPrayerTimes();

            btnValidateSalah.setOnClickListener(v -> startActivity(new Intent(this, SalahValidatorActivity.class)));

            btnPersonalInfo.setOnClickListener(v -> userProfileActivity());

            btnLogout.setOnClickListener(v -> logOut());
        }
    }

    private void userProfileActivity() {
        String userId = currentUser.getUid();
        Intent userProfileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
        userProfileIntent.putExtra("user_id", userId);
        startActivity(userProfileIntent);
    }

    private void logOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void executeFetchPrayerTimes() {
        SharedPreferences prefs = getSharedPreferences("prayer_times_cache_prefs", Context.MODE_PRIVATE);
        String cachedData = prefs.getString("prayer_times", null);
        long lastUpdated = prefs.getLong("last_updated", 0);
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        // Check if cached data is valid for today
        Calendar lastUpdatedCalendar = Calendar.getInstance();
        lastUpdatedCalendar.setTimeInMillis(lastUpdated);
        int lastUpdatedDay = lastUpdatedCalendar.get(Calendar.DAY_OF_MONTH);
        int lastUpdatedMonth = lastUpdatedCalendar.get(Calendar.MONTH) + 1;
        int lastUpdatedYear = lastUpdatedCalendar.get(Calendar.YEAR);

        if (cachedData != null && lastUpdatedDay == currentDay && lastUpdatedMonth == currentMonth && lastUpdatedYear == currentYear) {
            // Use cached data if it is from today
            processPrayerTimes(cachedData);
            return;
        }

        // If no valid cache, fetch new data
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            try {
                String address = "Peshawar Museum, Jamrud Road, Peshawar, Pakistan";
                int method = 1; // University of Islamic Sciences, Karachi
                int school = 1; // Hanafi
                String encodedAddress = URLEncoder.encode(address, "UTF-8");
                URL url = new URL("https://api.aladhan.com/v1/calendarByAddress/" + currentYear + "/" + currentMonth
                        + "?address=" + encodedAddress + "&method=" + method + "&school=" + school);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    StringBuilder jsonResponse = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        jsonResponse.append(inputLine);
                    }
                    return jsonResponse.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        try {
            String jsonResponse = future.get();
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                // Save to cache
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("prayer_times", jsonResponse);
                editor.putLong("last_updated", System.currentTimeMillis());
                editor.apply();

                runOnUiThread(() -> processPrayerTimes(jsonResponse));
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Error fetching prayer times. Please try again later.", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Error fetching data. Check your internet connection.", Toast.LENGTH_SHORT).show());
        } finally {
            executor.shutdown();
        }
    }


    private void processPrayerTimes(String jsonResponse) {
        try {
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray data = jsonObject.getJSONArray("data");
            JSONObject prayerData = data.getJSONObject(currentDay - 1); // Days are 0-based in the JSON response from the API
            JSONObject timings = prayerData.getJSONObject("timings");

            txtViewFajr.setText(formatTime(timings.getString("Fajr")));
            txtViewSunrise.setText(formatTime(timings.getString("Sunrise")));
            txtViewDhuhr.setText(formatTime(timings.getString("Dhuhr")));
            txtViewAsr.setText(formatTime(timings.getString("Asr")));
            txtViewMaghrib.setText(formatTime(timings.getString("Maghrib")));
            txtViewIsha.setText(formatTime(timings.getString("Isha")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Method to format time (remove any postfix and convert to 12-hour AM/PM format)
    private String formatTime(String time) {
        try {
            // Extract the time part and remove any trailing spaces
            String formattedTime = time.split("\\s+")[0].trim();

            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            Date date = inputFormat.parse(formattedTime);

            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return time; // Return the original time if parsing fails
        }
    }
}