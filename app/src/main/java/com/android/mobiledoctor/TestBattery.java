package com.android.mobiledoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.BATTERY;
import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.NETWORK;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestBattery extends AppCompatActivity {
    TextView batLevel, battHealth;
    isCharging receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_battery);
        batLevel = findViewById(R.id.battPercentage);
        battHealth = findViewById(R.id.batteryHealth);

        batLevel.setText("Battery Percentage: " + showBatPercentage() + "%");


        /*Register broadcast on create*/
        receiver = new isCharging();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, ifilter);


    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private int showBatPercentage() {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);


        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }


    /*  Inner broadcast class o listen to battery charging status*/
    public class isCharging extends BroadcastReceiver {

        public isCharging() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            if (status == BatteryManager.BATTERY_HEALTH_COLD) {

                battHealth.setText("Battery health: Cold");

            }
            if (status == BatteryManager.BATTERY_HEALTH_DEAD) {

                battHealth.setText("Battery health: Dead");
                setDefaults(BATTERY, FAILED, context);
            }
            if (status == BatteryManager.BATTERY_HEALTH_GOOD) {

                battHealth.setText("Battery health: Good");
                setDefaults(BATTERY, SUCCESS, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_OVERHEAT) {

                battHealth.setText("Battery health: Over Heat");

            }
            if (status == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {

                battHealth.setText("Battery health: Over Voltage");

            }
            if (status == BatteryManager.BATTERY_HEALTH_UNKNOWN) {

                battHealth.setText("Battery health: Unknown");

            }
            if (status == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {

                battHealth.setText("Battery health: Unspecified failure");
                setDefaults(BATTERY, FAILED, context);

            }
        }


    }
}
