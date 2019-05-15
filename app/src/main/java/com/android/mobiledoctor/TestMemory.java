package com.android.mobiledoctor;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.MEMORY;
import static com.android.mobiledoctor.MainActivity.POWER;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestMemory extends AppCompatActivity {
        TextView textView ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_test_memory);
            textView = findViewById(R.id.textView);

        }

    @Override
    protected void onResume() {
        super.onResume();

        if (isHighPerformingDevice()){
            setDefaults(MEMORY, SUCCESS, this);
            textView.setText("Memory Test Passed");
        }else{
            setDefaults(MEMORY, FAILED, this);
            textView.setText("Not Enough Memory\nWe have found the following problems:\nAvailable processor " +
                    "running on your device is less than 4 which is considered below average\nOR\nRam capability is" +
                    " is critically low\nOR\nThe memory class is less thaN the considered amount");
        }
        timer(5000, this);
    }

    private void timer(long delayMillis, final Context context) {
        Runnable timerTask = new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = findViewById(R.id.progress);
                TextView textView2 = findViewById(R.id.progressText);
                textView2.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);


            }
        };
        Handler handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }
        private boolean isHighPerformingDevice(){

               ActivityManager activityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                return !activityManager.isLowRamDevice() && Runtime.getRuntime().availableProcessors() >= 4 && activityManager.getMemoryClass() >= 128;
            }
        }


