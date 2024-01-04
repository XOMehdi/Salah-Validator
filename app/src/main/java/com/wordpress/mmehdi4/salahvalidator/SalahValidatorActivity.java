package com.wordpress.mmehdi4.salahvalidator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SalahValidatorActivity extends AppCompatActivity implements SensorEventListener {

    private int qiyamTargetCount, rukuTargetCount, sajdahTargetCount, jalsaTargetCount;
    private int qiyamPerformedCount, rukuPerformedCount, sajdahPerformedCount, jalsaPerformedCount;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isUpright = false;
    private boolean isHorizontal = false;
    private boolean isAngled = false;

    private TextView txtViewQiyamTargetCount,txtViewQiyamPerformedCount, txtViewRukuTargetCount, txtViewRukuPerformedCount,
            txtViewSajdahTargetCount, txtViewSajdahPerformedCount, txtViewJalsaTargetCount, txtViewJalsaPerformedCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salah_validator);

        int totalRakah = 2;
        initTargetCounts(totalRakah);

        txtViewQiyamTargetCount = findViewById(R.id.txt_view_qiyam_target_count);
        txtViewQiyamPerformedCount = findViewById(R.id.txt_view_qiyam_performed_count);
        txtViewRukuTargetCount = findViewById(R.id.txt_view_ruku_target_count);
        txtViewRukuPerformedCount = findViewById(R.id.txt_view_ruku_performed_count);
        txtViewSajdahTargetCount = findViewById(R.id.txt_view_sajdah_target_count);
        txtViewSajdahPerformedCount = findViewById(R.id.txt_view_sajdah_performed_count);
        txtViewJalsaTargetCount = findViewById(R.id.txt_view_jalsa_target_count);
        txtViewJalsaPerformedCount = findViewById(R.id.txt_view_jalsa_performed_count);
        ToggleButton tglBtnStartStop = findViewById(R.id.tgl_btn_start_stop);

        txtViewQiyamTargetCount.setText(String.valueOf(qiyamTargetCount));
        txtViewRukuTargetCount.setText(String.valueOf(rukuTargetCount));
        txtViewSajdahTargetCount.setText(String.valueOf(sajdahTargetCount));
        txtViewJalsaTargetCount.setText(String.valueOf(jalsaTargetCount));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        tglBtnStartStop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startSensor();
            } else {
                stopSensor();
            }
        });
    }

    private void initTargetCounts(int totalRakah) {

        if (totalRakah == 1) {
            qiyamTargetCount = 2;
            rukuTargetCount = 1;
            sajdahTargetCount = 2;
            jalsaTargetCount = 1;
        } else if (totalRakah == 2) {
            qiyamTargetCount = 4;
            rukuTargetCount = 2;
            sajdahTargetCount = 4;
            jalsaTargetCount = 3;
        } else if (totalRakah == 3) {
            qiyamTargetCount = 6;
            rukuTargetCount = 3;
            sajdahTargetCount = 6;
            jalsaTargetCount = 5;
        } else {
            qiyamTargetCount = 8;
            rukuTargetCount = 4;
            sajdahTargetCount = 8;
            jalsaTargetCount = 6;
        }
    }

    private void initPerformedCounts() {
        qiyamPerformedCount = 0;
        rukuPerformedCount = 0;
        sajdahPerformedCount = 0;
        jalsaPerformedCount = 0;
    }

    private void startSensor() {

        initPerformedCounts();

        updateQiyamCount();
        updateRukuCount();
        updateSajdahCount();
        updateJalsaCount();

        if (accelerometerSensor != null) {
            // Register the sensor listener
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Sensor Started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Accelerometer not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopSensor() {
        // Unregister the sensor listener
        sensorManager.unregisterListener(this);
        Toast.makeText(this, "Sensor Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this application
    }

    private void updateQiyamCount() {
        txtViewQiyamPerformedCount.setText(String.valueOf(qiyamPerformedCount));
    }

    private void updateRukuCount() {
        txtViewRukuPerformedCount.setText(String.valueOf(rukuPerformedCount));
    }

    private void updateSajdahCount() {
        txtViewSajdahPerformedCount.setText(String.valueOf(sajdahPerformedCount));
    }

    private void updateJalsaCount() {
        txtViewJalsaPerformedCount.setText(String.valueOf(jalsaPerformedCount));
    }

    private boolean isUprightPosition(float x, float y, float z) {
        // Check for Qiyam position
        // Assume the phone bottom is pointing negative y axis,
        // and the device's top is pointing positive y axis or the device is sitting along the y axis
        boolean isQiyam = Math.abs(x) < 2 && Math.abs(y) > 8 && Math.abs(z) < 2;

        Toast.makeText(this, String.format("Qiyam: x=%.2f, y=%.2f, z=%.2f", x, y, z), Toast.LENGTH_SHORT).show();

        return isQiyam;
    }

    private boolean isHorizontalPosition(float x, float y, float z) {
        // Check for Ruku position
        // Assume the phone bottom is pointing negative x axis,
        // and the device's top is pointing positive x axis or the device is sitting along the x axis
        boolean isRuku = Math.abs(x) < 2 && Math.abs(y) < 2 && z > -8;

        Toast.makeText(this, String.format("Ruku: x=%.2f, y=%.2f, z=%.2f", x, y, z), Toast.LENGTH_SHORT).show();

        return isRuku;
    }

    private boolean isAngledPosition(float x, float y, float z) {
        // Check for the specified angled position
        // Assume the device is at an angle of 35 to 60 degrees with the ground

        // Adjusted conditions for the specified angled position
        boolean isSajdah = Math.abs(x) < 2 && Math.abs(y) > 3 && Math.abs(y) < 8 && Math.abs(z) < 2;

        Toast.makeText(this, String.format("Angled: x=%.2f, y=%.2f, z=%.2f", x, y, z), Toast.LENGTH_SHORT).show();

        return isSajdah;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            boolean isUprightNow = isUprightPosition(x, y, z);

            // Position changed to upright
            if (isUprightNow && !isUpright) {
                qiyamPerformedCount++;
                updateQiyamCount();
            }
            isUpright = isUprightNow;

            boolean isHorizontalNow = isHorizontalPosition(x, y, z);

            // Position changed to horizontal
            if (isHorizontalNow && !isHorizontal) {
                rukuPerformedCount++;
                updateRukuCount();
            }
            isHorizontal = isHorizontalNow;

            boolean isAngledNow = isAngledPosition(x, y, z);

            // Position changed to angled
            if (isAngledNow && !isAngled) {
                sajdahPerformedCount++;
                updateSajdahCount();
            }
            isAngled = isAngledNow;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSensor(); // Ensure to stop the sensor when the activity is destroyed
    }
}