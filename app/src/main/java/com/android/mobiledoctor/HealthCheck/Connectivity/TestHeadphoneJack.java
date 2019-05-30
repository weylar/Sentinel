package com.android.mobiledoctor.HealthCheck.Connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.R;

import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.HEADPHONE;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;

public class TestHeadphoneJack extends AppCompatActivity {
    TextView  result, skip, reason;
    Button move;
    Runnable timerTask;
    Handler handler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_headphone_jack);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        result = findViewById(R.id.result);
        reason = findViewById(R.id.reason);
        skip = findViewById(R.id.skip);
        move = findViewById(R.id.move);
        progressBar = findViewById(R.id.progress);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(HEADPHONE, UNCHECKED, TestHeadphoneJack.this);
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
                setDefaults(HEADPHONE, UNCHECKED, this);
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
        setDefaults(HEADPHONE, UNCHECKED, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetStateReceiver receiver = new HeadsetStateReceiver();
        registerReceiver( receiver, receiverFilter );
        runProgress();
        timer(20000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timerTask);

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

    private void warnNoHeadphone() {
        reason.setVisibility(View.VISIBLE);
        reason.setText(getResources().getString(R.string.insert_headphone));
        result.setVisibility(View.GONE);
        move.setVisibility(View.GONE);
        skip.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void passAction() {
        setDefaults(HEADPHONE, SUCCESS, TestHeadphoneJack.this);
        result.setVisibility(View.VISIBLE);
        result.setText("PASS");
        reason.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
        handler.removeCallbacks(timerTask);
    }

    private void failAction() {
        setDefaults(HEADPHONE, FAILED, TestHeadphoneJack.this);
        reason.setVisibility(View.GONE);
        result.setVisibility(View.VISIBLE);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        progressBar.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
    }

    public class HeadsetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if ((action.equals(Intent.ACTION_HEADSET_PLUG)))
            {
                int headSetState = intent.getIntExtra("state", 0);
                if (headSetState == 0) {
                    warnNoHeadphone();


                }else{
                    passAction();


                }
            }
        }
    }
}
