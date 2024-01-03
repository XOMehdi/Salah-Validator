package com.wordpress.mmehdi4.salahvalidator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
            new FetchPrayerTimesTask().execute();

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

    private class FetchPrayerTimesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();

            try {
                String address = "Peshawar Museum, Jamrud Road, Peshawar, Pakistan";
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
                int year = calendar.get(Calendar.YEAR);
                int method = 1; // University of Islamic Sciences, Karachi
                int school = 1; // Hanafi

                String encodedAddress = URLEncoder.encode(address, "UTF-8");
                URL url = new URL("https://api.aladhan.com/v1/calendarByAddress/" + year + "/" + month + "?address="
                        + encodedAddress + "&method=" + method + "&school=" + school);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        result.append(inputLine);
                    }
                }

                urlConnection.disconnect();

                // Print the JSON response for debugging
                System.out.println("JSON Response: " + result);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                processPrayerTimes(result);
            } else {
                // Handle the case where the response is empty or invalid
                // You might want to show an error message or take appropriate action
            }
        }
    }

    private void processPrayerTimes(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray data = jsonObject.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject prayerData = data.getJSONObject(i);

                // Check if "timings" key is present in the prayerData object
                if (prayerData.has("timings")) {
                    JSONObject timings = prayerData.getJSONObject("timings");

                    // Format and display the time without any postfix
                    txtViewFajr.setText(formatTime(timings.getString("Fajr")));
                    txtViewSunrise.setText(formatTime(timings.getString("Sunrise")));
                    txtViewDhuhr.setText(formatTime(timings.getString("Dhuhr")));
                    txtViewAsr.setText(formatTime(timings.getString("Asr")));
                    txtViewMaghrib.setText(formatTime(timings.getString("Maghrib")));
                    txtViewIsha.setText(formatTime(timings.getString("Isha")));

                    break; // Exit the loop once the prayer times are set
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Method to format time (remove any postfix and convert to 12-hour AM/PM format)
    private String formatTime(String time) {
        try {
            // Extract the time part and remove any trailing spaces
            String formattedTime = time.split("\\s+")[0].trim();

            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            Date date = inputFormat.parse(formattedTime);
            return outputFormat.format(date);
        } catch (ParseException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return time; // Return the original time if parsing fails
        }
    }

}
