package com.android.sentinel;

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

import com.android.sentinel.HealthCheck.Buttons.TestHomeButton;
import com.android.sentinel.HealthCheck.Display.MulitouchEntry;
import com.android.sentinel.HealthCheck.HealthCheck;

import static com.android.sentinel.HealthCheck.TestFragment.BACK;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.RECENT;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestRecent extends AppCompatActivity {
    TextView textView;
    Handler handler;
    Runnable timerTask;
    ProgressBar progressBar;
    TextView result, skip, explanation, btn;
    Thread thread;
    InnerReceiver innerReceiver;
    IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_recent);
        textView = findViewById(R.id.text);
        progressBar = findViewById(R.id.progress);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        explanation = findViewById(R.id.explain);
        btn = findViewById(R.id.button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(RECENT, UNCHECKED, TestRecent.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(BACK)) {
                        Intent intent = new Intent(TestRecent.this,
                                MulitouchEntry.class);
                        intent.putExtra(FROM, RECENT);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });

        intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, intentFilter);
        handler = new Handler();
        timer(10000);
        runProgress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(RECENT, UNCHECKED, this);
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
        setDefaults(RECENT, UNCHECKED, this);


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
                setFail(TestRecent.this);
            }
        };
        handler.postDelayed(timerTask, delayMillis);


    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timerTask);
    }

    private void setFail(Context context) {
        setDefaults(RECENT, FAILED, context);
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
                    if (val.equals(BACK)) {
                        Intent intent = new Intent(TestRecent.this,
                                MulitouchEntry.class);
                        intent.putExtra(FROM, RECENT);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });

        handler.removeCallbacks(timerTask);
        unregisterReceiver(innerReceiver);

    }

    private void setPass(Context context) {
        setDefaults(RECENT, SUCCESS, context);
        result.setText("PASS");
        result.setTextColor(getResources().getColor(R.color.green));
        btn.setText("Continue");
        btn.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        handler.removeCallbacks(timerTask);
        unregisterReceiver(innerReceiver);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(BACK)) {
                        Intent intent = new Intent(TestRecent.this,
                                MulitouchEntry.class);
                        intent.putExtra(FROM, RECENT);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });
    }

    class InnerReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {

                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        setPass(TestRecent.this);
                    }

                }

            }
        }
    }


}
