package com.wordpress.mmehdi4.salahvalidator;

import android.hardware.SensorEventListener;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class SalahValidatorFragment extends Fragment implements SensorEventListener {

    private int qiyamTargetCount, rukuTargetCount, sajdahTargetCount, jalsaTargetCount;
    private int qiyamPerformedCount, rukuPerformedCount, sajdahPerformedCount, jalsaPerformedCount;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private static final String TAG = "MotionDebug";

    private boolean isUpright = false;
    private boolean isHorizontal = false;
    private boolean isAngled = false;

    private TextView txtViewQiyamPerformedCount;
    private TextView txtViewRukuPerformedCount;
    private TextView txtViewSajdahPerformedCount;
    private TextView txtViewJalsaPerformedCount;

    public SalahValidatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salah_validator, container, false);

        int totalRakah = 2;
        initTargetCounts(totalRakah);
        initPerformedCounts();

        // Initialize UI components
        TextView txtViewQiyamTargetCount = view.findViewById(R.id.txt_view_qiyam_target_count);
        txtViewQiyamPerformedCount = view.findViewById(R.id.txt_view_qiyam_performed_count);
        TextView txtViewRukuTargetCount = view.findViewById(R.id.txt_view_ruku_target_count);
        txtViewRukuPerformedCount = view.findViewById(R.id.txt_view_ruku_performed_count);
        TextView txtViewSajdahTargetCount = view.findViewById(R.id.txt_view_sajdah_target_count);
        txtViewSajdahPerformedCount = view.findViewById(R.id.txt_view_sajdah_performed_count);
        TextView txtViewJalsaTargetCount = view.findViewById(R.id.txt_view_jalsa_target_count);
        txtViewJalsaPerformedCount = view.findViewById(R.id.txt_view_jalsa_performed_count);
        ToggleButton tglBtnStartStop = view.findViewById(R.id.tgl_btn_start_stop);

        // Set target counts
        txtViewQiyamTargetCount.setText(String.valueOf(qiyamTargetCount));
        txtViewRukuTargetCount.setText(String.valueOf(rukuTargetCount));
        txtViewSajdahTargetCount.setText(String.valueOf(sajdahTargetCount));
        txtViewJalsaTargetCount.setText(String.valueOf(jalsaTargetCount));

        // Set performed counts
        txtViewQiyamPerformedCount.setText(String.valueOf(qiyamPerformedCount));
        txtViewRukuPerformedCount.setText(String.valueOf(rukuPerformedCount));
        txtViewSajdahPerformedCount.setText(String.valueOf(sajdahPerformedCount));
        txtViewJalsaPerformedCount.setText(String.valueOf(jalsaPerformedCount));

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Toggle button listener
        tglBtnStartStop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startSensor();
            } else {
                stopSensor();
                Toast.makeText(getActivity(), "Sensor Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(getActivity(), "Sensor Started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Accelerometer not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopSensor() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
        boolean isQiyam = Math.abs(x) < 2 && Math.abs(y) > 8 && Math.abs(z) < 2;

        Log.d(TAG, String.format("Qiyam Check - x=%.2f, y=%.2f, z=%.2f, Result: %b", x, y, z, isQiyam));

        return isQiyam;
    }

    private boolean isHorizontalPosition(float x, float y, float z) {
        boolean isRuku = Math.abs(x) < 2 && Math.abs(y) < 2 && z > -8;

        Log.d(TAG, String.format("Ruku Check - x=%.2f, y=%.2f, z=%.2f, Result: %b", x, y, z, isRuku));

        return isRuku;
    }

    private boolean isAngledPosition(float x, float y, float z) {
        // Fine-tune the Sajdah angle conditions
        boolean isSajdah = Math.abs(x) < 2 && Math.abs(y) > 3 && Math.abs(y) < 8 && Math.abs(z) < 2;

        Log.d(TAG, String.format("Sajdah Check - x=%.2f, y=%.2f, z=%.2f, Result: %b", x, y, z, isSajdah));

        return isSajdah;
    }

    private void logRawSensorValues(float x, float y, float z) {
        // Log raw accelerometer readings for calibration and debugging
        Log.d(TAG, String.format("Raw Sensor Values - x=%.2f, y=%.2f, z=%.2f", x, y, z));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            logRawSensorValues(x, y, z);

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
    public void onDestroyView() {
        super.onDestroyView();
        stopSensor();
    }
}