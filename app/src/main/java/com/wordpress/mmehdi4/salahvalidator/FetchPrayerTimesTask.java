package com.wordpress.mmehdi4.salahvalidator;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

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

public class FetchPrayerTimesTask extends AsyncTask<Void, Void, String> {
    private final TextView txtViewFajr, txtViewSunrise, txtViewDhuhr, txtViewAsr, txtViewMaghrib, txtViewIsha;
    private final MainActivity mainActivity;
    public FetchPrayerTimesTask(MainActivity mainActivity, TextView txtViewFajr, TextView txtViewSunrise, TextView txtViewDhuhr,
                                TextView txtViewAsr, TextView txtViewMaghrib, TextView txtViewIsha) {
        this.mainActivity = mainActivity;
        this.txtViewFajr = txtViewFajr;
        this.txtViewSunrise = txtViewSunrise;
        this.txtViewDhuhr = txtViewDhuhr;
        this.txtViewAsr = txtViewAsr;
        this.txtViewMaghrib = txtViewMaghrib;
        this.txtViewIsha = txtViewIsha;
    }


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
            mainActivity.runOnUiThread(() ->
                    Toast.makeText(mainActivity, "Error fetching prayer times. Please try again later.", Toast.LENGTH_SHORT).show()
            );
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
