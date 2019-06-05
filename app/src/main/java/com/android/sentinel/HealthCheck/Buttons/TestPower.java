package com.android.sentinel.HealthCheck.Buttons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.POWER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestPower extends AppCompatActivity {
    BroadcastReceiver powerDetect;
    IntentFilter intentFilter;
    TextView textView;
    Handler handler;
    Runnable timerTask;
    ProgressBar progressBar;
    TextView result, skip, explanation, btn;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_power);
        textView = findViewById(R.id.text);
        progressBar = findViewById(R.id.progress);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        explanation = findViewById(R.id.explain);
        btn = findViewById(R.id.button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(POWER, UNCHECKED, TestPower.this);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(POWER, UNCHECKED, this);
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
        setDefaults(POWER, UNCHECKED, this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        powerDetect = new PowerDetect();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerDetect, intentFilter);
        timer(10000);
        runProgress();

    }

    private void runProgress() {
        thread = new Thread() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerDetect);
        handler.removeCallbacks(timerTask);
    }

    private void timer(long delayMillis) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                setFail();
            }
        };
        handler.postDelayed(timerTask, delayMillis);


    }

    private void setFail() {
        setDefaults(POWER, FAILED, TestPower.this);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        btn.setText("Continue");
        progressBar.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestPower.this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(POWER, UNCHECKED, TestPower.this);
                finish();
            }
        });
    }

    private void setPass(Context context) {
        setDefaults(POWER, SUCCESS, context);
        result.setText("PASS");
        result.setTextColor(getResources().getColor(R.color.green));
        btn.setText("Continue");
        btn.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        handler.removeCallbacks(timerTask);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestPower.this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    class PowerDetect extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                setPass(context);
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                setPass(context);
            }
        }
    }




}
