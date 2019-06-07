package com.android.sentinel.HealthCheck.Display;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.Camera.TestFlash;
import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.BATTERY;
import static com.android.sentinel.HealthCheck.TestFragment.DIMMING;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestDimming extends AppCompatActivity {
    Button pass, fail;
    TextView skip, brightnessLevel;
    float val = 0;
    Runnable timerTask;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_dimming);
        pass = findViewById(R.id.pass);
        fail = findViewById(R.id.fail);
        skip = findViewById(R.id.skip);
        brightnessLevel = findViewById(R.id.brightness_level);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(DIMMING, UNCHECKED, TestDimming.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(BATTERY)) {
                        Intent intent = new Intent(TestDimming.this, TestFlash.class);
                        intent.putExtra(FROM, DIMMING);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(DIMMING, SUCCESS, TestDimming.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(BATTERY)) {
                        Intent intent = new Intent(TestDimming.this, TestFlash.class);
                        intent.putExtra(FROM, DIMMING);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });

        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(DIMMING, FAILED, TestDimming.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(BATTERY)) {
                        Intent intent = new Intent(TestDimming.this, TestFlash.class);
                        intent.putExtra(FROM, DIMMING);
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
                setDefaults(DIMMING, UNCHECKED, this);
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
        setDefaults(DIMMING, UNCHECKED, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        timer(1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timerTask);
    }

    private void dim(float range) {
        float brightness = range / (float) 100;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);
        brightnessLevel.setText("Backlight brightness now set to " + (int) range + "%");
    }

    private void timer(final long delayMillis) {

        timerTask = new Runnable() {
            @Override
            public void run() {
                if (val > 100) {
                    val = 0;
                    dim(val);
                } else {
                    dim(val);
                    val += 25;
                }

                handler.postDelayed(timerTask, delayMillis);
            }
        };

        handler.post(timerTask);

    }


}
