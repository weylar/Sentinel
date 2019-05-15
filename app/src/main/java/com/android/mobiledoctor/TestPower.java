package com.android.mobiledoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.POWER;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.VOLUME;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestPower extends AppCompatActivity {
    BroadcastReceiver powerDetect;
    IntentFilter intentFilter;
    TextView textView;
    Handler handler;
    Runnable timerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_power);
        textView = findViewById(R.id.text);

        powerDetect = new PowerDetect();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerDetect, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        powerDetect = new PowerDetect();
       intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerDetect, intentFilter);
        timer(15000, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(powerDetect);
        handler.removeCallbacks(timerTask);

    }

    private void timer(long delayMillis, final Context context) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                setDefaults(POWER, FAILED, context);
                Toast.makeText(context, "Sorry! Test Failed!", Toast.LENGTH_SHORT).show();
                finish();

            }
        };
        handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);

    }


    class PowerDetect extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                setDefaults(POWER, SUCCESS, context);
                finish();
                Toast.makeText(context, "Woo! Test Passed!", Toast.LENGTH_SHORT).show();
            }

            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                setDefaults(POWER, SUCCESS, context);
                finish();
                Toast.makeText(context, "Woo! Test Passed!", Toast.LENGTH_SHORT).show();

            }else{
                timer(10000, context);
            }
        }
    }


}
