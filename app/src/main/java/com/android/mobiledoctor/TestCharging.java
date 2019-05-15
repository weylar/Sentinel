package com.android.mobiledoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.BATTERY;
import static com.android.mobiledoctor.MainActivity.CHARGING;
import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.NETWORK;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestCharging extends AppCompatActivity {
    private static final int MESSAGE = 1;
    TextView isCharging, isUsbCharging;
    IntentFilter ifilter;
    Runnable timerTask;
    Handler handler;
    BroadcastReceiver chargingBroadcast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_charging);
        isCharging = findViewById(R.id.isCharging);
        isUsbCharging = findViewById(R.id.isUsbCharging);
        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        chargingBroadcast = new ChargingBroadcast();
        registerReceiver(chargingBroadcast, ifilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chargingBroadcast);
        //if (handler.hasMessages(MESSAGE)) {
            handler.removeCallbacks(timerTask);
           // Log.e("Message", "Removed Here");
           // handler.removeMessages(MESSAGE);
        //}
    }

    private void timer(long delayMillis, final Context context) {
         timerTask = new Runnable() {
            @Override
            public void run() {
                setDefaults(CHARGING, FAILED, context);
                Toast.makeText(context, "Sorry! Test Failed!", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        handler.postDelayed(timerTask, delayMillis);
    }
    private class ChargingBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL) {
                isCharging.setText("Battery Charger Connection Status: Charging");
                setDefaults(CHARGING, SUCCESS, context);
                isUsbCharging.setVisibility(View.VISIBLE);

            } else {
                isCharging.setText("Battery Charger Connection Status: Not Charging\n Please connect your charger!");
                isUsbCharging.setVisibility(View.GONE);
                timer(20000, context);
                Toast.makeText(context, "Not Charging", Toast.LENGTH_SHORT).show();
            }
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                isUsbCharging.setText("Charging Mode:  USB");
            } else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                isUsbCharging.setText("Charging Mode: AC");

            }


        }

    }
}



