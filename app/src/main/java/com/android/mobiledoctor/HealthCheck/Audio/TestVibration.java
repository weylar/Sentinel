package com.android.mobiledoctor.HealthCheck.Audio;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.R;

import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.VIBRATOR;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;

public class TestVibration extends AppCompatActivity {
    Button pass, fail;
    TextView skip;
    Context context = this;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_vibration);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pass = findViewById(R.id.pass);
        fail = findViewById(R.id.fail);
        skip = findViewById(R.id.skip);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(VIBRATOR, SUCCESS, context);
                finish();
            }
        });
        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(VIBRATOR, FAILED, context);
                finish();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(VIBRATOR, UNCHECKED, context);
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        v.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vibrate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(VIBRATOR, UNCHECKED, this);
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
        setDefaults(VIBRATOR, UNCHECKED, this);
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(20000000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(20000000);
        }
    }
}
