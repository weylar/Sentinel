package com.android.sentinel.HealthCheck.Battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.Display.TestDimming;
import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.BATTERY;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestBattery extends AppCompatActivity {
    TextView batLevel, battHealth, result;
    isCharging receiver;
    String val = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_battery);
        batLevel = findViewById(R.id.battPercentage);
        battHealth = findViewById(R.id.batteryHealth);
        result = findViewById(R.id.result);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onResume() {
        super.onResume();
        batLevel.setText("Battery Percentage - " + showBatPercentage() + "%");
        receiver = new isCharging();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
    }

    public void move(View view) {
        if (getIntent().getExtras() != null) {
            val = getIntent().getStringExtra(FROM);
            if (val.equals("Home")) {
                Intent intent = new Intent(this, TestDimming.class);
                intent.putExtra(FROM, BATTERY);
                startActivity(intent);
            }
        }else {
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(BATTERY, UNCHECKED, this);
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
        setDefaults(BATTERY, UNCHECKED, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public   int showBatPercentage() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, intentFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }


    /*  Inner broadcast class o listen to battery charging status*/
    public class isCharging extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            if (status == BatteryManager.BATTERY_HEALTH_COLD) {
                battHealth.setText("Battery Health - Cold");
                result.setText("Fail");
                setDefaults(BATTERY, FAILED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_DEAD) {
                battHealth.setText("Battery Health - Dead");
                result.setText("Fail");
                setDefaults(BATTERY, FAILED, context);
            }
            if (status == BatteryManager.BATTERY_HEALTH_GOOD) {
                battHealth.setText("Battery Health - Good");
                result.setText("PASS");
                setDefaults(BATTERY, SUCCESS, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                battHealth.setText("Battery Health - Overheat");
                result.setText("Fail");
                setDefaults(BATTERY, FAILED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                battHealth.setText("Battery Health - Over Voltage");
                result.setText("Fail");
                setDefaults(BATTERY, FAILED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_UNKNOWN) {
                battHealth.setText("Battery Health - Unknown");
                result.setText("Unknown");
                setDefaults(BATTERY, UNCHECKED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                battHealth.setText("Battery Health - Unspecified Failure");
                result.setText("Fail");
                setDefaults(BATTERY, FAILED, context);

            }
        }


    }
}
