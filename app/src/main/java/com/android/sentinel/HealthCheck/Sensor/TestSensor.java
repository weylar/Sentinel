package com.android.sentinel.HealthCheck.Sensor;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.SENSOR;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestSensor extends AppCompatActivity implements SensorEventListener {
    TextView sensorState, sensorStateReading, result, skip, detail;
    Button move;
    ProgressBar progressBar;
    Sensor mSensor;
    SensorManager mSensorManager;
    float maxRange;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensor);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        maxRange = mSensor.getMaximumRange();
        result = findViewById(R.id.result);
        detail = findViewById(R.id.detail);
        sensorState = findViewById(R.id.sensor_state);
        sensorStateReading = findViewById(R.id.sensor_state_reading);
        skip = findViewById(R.id.skip);
        move = findViewById(R.id.move);
        progressBar = findViewById(R.id.progress);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(SENSOR, UNCHECKED, TestSensor.this);
                finish();
            }
        });

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(SENSOR, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setDefaults(SENSOR, UNCHECKED, this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float millibarsOfProximeter = event.values[0];
        sensorState.setVisibility(View.VISIBLE);
        sensorStateReading.setVisibility(View.VISIBLE);
        if (millibarsOfProximeter == 0){
           sensorState.setText("Uncover sensor.");
           sensorStateReading.setText("Proximity reading - NEAR");
            passAction();
        }else{
            sensorState.setText("Cover sensor.");
            sensorStateReading.setText("Proximity reading - FAR");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            timer(10000);
            runProgress();
        } else {
            failAction();
        }
    }

    private void runProgress() {
       Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= 10; i++) {
                    if (i > 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setMax(10);
                    progressBar.setProgress(i);

                }
            }
        };

        thread.start();
    }

    private void timer(long delayMillis) {
        runnable = new Runnable() {
            @Override
            public void run() {
               failAction();
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, delayMillis);


    }

    private void passAction() {
        setDefaults(SENSOR, SUCCESS, this);
        result.setVisibility(View.VISIBLE);
        result.setText("PASS");
        progressBar.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
    }

    private void failAction() {
        //detail.setText("No sensor detected on device.");
        setDefaults(SENSOR, FAILED, this);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        sensorState.setVisibility(View.GONE);
        sensorStateReading.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
    }




}
