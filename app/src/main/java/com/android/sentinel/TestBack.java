package com.android.sentinel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.Buttons.TestHomeButton;
import com.android.sentinel.HealthCheck.HealthCheck;

import static com.android.sentinel.HealthCheck.TestFragment.BACK;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.POWER;
import static com.android.sentinel.HealthCheck.TestFragment.PRIMARY_CAMERA;
import static com.android.sentinel.HealthCheck.TestFragment.RECENT;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.VOLUME;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestBack extends AppCompatActivity {
    TextView textView;
    Handler handler;
    Runnable timerTask;
    ProgressBar progressBar;
    TextView result, skip, explanation, btn;
    Thread thread;
    int backCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_back);
        textView = findViewById(R.id.text);
        progressBar = findViewById(R.id.progress);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        explanation = findViewById(R.id.explain);
        btn = findViewById(R.id.button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(BACK, UNCHECKED, TestBack.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(VOLUME)) {
                        Intent intent = new Intent(TestBack.this,
                                TestRecent.class);
                        intent.putExtra(FROM, BACK);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(BACK, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        backCount++;
        if (backCount > 1) {
            super.onBackPressed();
            setDefaults(BACK, UNCHECKED, this);
        }else{
            setPass(this);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
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


    private void timer(long delayMillis) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                setFail(TestBack.this);
            }
        };
        handler.postDelayed(timerTask, delayMillis);


    }

    private void setFail(Context context) {
        setDefaults(BACK, FAILED, context);
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
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(VOLUME)) {
                        Intent intent = new Intent(TestBack.this,
                                TestRecent.class);
                        intent.putExtra(FROM, BACK);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });

        handler.removeCallbacks(timerTask);

    }

    private void setPass(Context context) {
        setDefaults(BACK, SUCCESS, context);
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
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(VOLUME)) {
                        Intent intent = new Intent(TestBack.this,
                                TestRecent.class);
                        intent.putExtra(FROM, BACK);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });
    }






}
