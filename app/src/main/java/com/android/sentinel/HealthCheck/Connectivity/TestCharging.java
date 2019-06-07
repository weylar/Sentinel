package com.android.sentinel.HealthCheck.Connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.HealthCheck.Sensor.TestCompass;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.CHARGING;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.HEADPHONE;
import static com.android.sentinel.HealthCheck.TestFragment.NETWORK;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestCharging extends AppCompatActivity {
    TextView isCharging, isUsbCharging, result, skip;
    Button move;
    IntentFilter ifilter;
    Runnable timerTask;
    Handler handler;
    BroadcastReceiver chargingBroadcast;
    ProgressBar progressBar;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_charging);
        isCharging = findViewById(R.id.isCharging);
        isUsbCharging = findViewById(R.id.isUsbCharging);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        move = findViewById(R.id.move);
        progressBar = findViewById(R.id.progress);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(CHARGING, UNCHECKED, TestCharging.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(HEADPHONE)) {
                        Intent intent = new Intent(TestCharging.this, TestCompass.class);
                        intent.putExtra(FROM, CHARGING);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(HEADPHONE)) {
                        Intent intent = new Intent(TestCharging.this, TestCompass.class);
                        intent.putExtra(FROM, CHARGING);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        chargingBroadcast = new ChargingBroadcast();
        registerReceiver(chargingBroadcast, ifilter);
        runProgress();
        timer(20000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chargingBroadcast);
        handler.removeCallbacks(timerTask);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(CHARGING, UNCHECKED, this);
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
        setDefaults(CHARGING, UNCHECKED, this);
    }

    private void runProgress() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= 10; i++) {
                    if (i > 0) {
                        try {
                            sleep(2000);
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

    private void passAction() {
        result.setVisibility(View.VISIBLE);
        result.setText("PASS");
        progressBar.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
        handler.removeCallbacks(timerTask);
        isCharging.setVisibility(View.VISIBLE);
        isCharging.setText("Charger Connection Status - Charging");
        setDefaults(CHARGING, SUCCESS, this);
        isUsbCharging.setVisibility(View.VISIBLE);


    }

    private void failAction() {
        setDefaults(CHARGING, FAILED, TestCharging.this);
        isCharging.setVisibility(View.GONE);
        result.setVisibility(View.VISIBLE);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        progressBar.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
    }

    private void timer(long delayMillis) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                failAction();
            }
        };
        handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }

    private class ChargingBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            chargingModeUpdateUI(chargePlug);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
                passAction();
            } else {
                updateNotCharging();
            }

        }

    }

    private void updateNotCharging() {
        isCharging.setVisibility(View.VISIBLE);
        skip.setVisibility(View.VISIBLE);
        move.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        isCharging.setText("Charger Connection Status - Not Charging\n\n Please connect your charger!");
        isUsbCharging.setVisibility(View.GONE);
        result.setVisibility(View.GONE);
    }

    private void chargingModeUpdateUI(int chargePlug) {
        if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
            isUsbCharging.setText("Charging Mode:  USB");
        } else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
            isUsbCharging.setText("Charging Mode: AC");

        }
    }
}



