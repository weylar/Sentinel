package com.android.sentinel.HealthCheck.Display;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.MULTITOUCH;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.VOLUME;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestMultitouch extends AppCompatActivity {
    Toast toast;
    View customView;
    Runnable runnable;
    Handler handler;
    TextView countdown;
    int number = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multitouch);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        countdown = findViewById(R.id.countdown);
        customView = getLayoutInflater().inflate(R.layout.sound_toast, (ViewGroup) findViewById(R.id.rel));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        if (index == 1) {
            vibrate();
            passAction();
        }
        return super.onTouchEvent(event);
    }


    public void countDown(final long wait){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                number--;
                countdown.setText(number + "");
                handler.postDelayed(this, wait);
            }
        };
        runnable.run();
    }
    public void setTimeout(long milliSec){
        runnable = new Runnable() {
            @Override
            public void run() {
                failAction();
            }
        };
        handler.postDelayed(runnable, milliSec);
    }
    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        countDown(1000);
        setTimeout(15000);
        toast = Toast.makeText(this, "Toast:Gravity.CENTER", Toast.LENGTH_SHORT);
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
        showToast();
    }

    private void showToast() {
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setView(customView);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toast.cancel();

    }

    private void failAction() {
        Intent intentNo = new Intent(TestMultitouch.this, MulitouchEntry.class);
        intentNo.putExtra("FROM_MULTITOUCH", "yes");
        intentNo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getIntent().getStringExtra(FROM) != null)
        {intentNo.putExtra(FROM, VOLUME);}
        startActivity(intentNo);
        setDefaults(MULTITOUCH, FAILED, TestMultitouch.this);
        handler.removeCallbacks(runnable);
    }

    private void passAction() {
        Intent intentYes = new Intent(TestMultitouch.this, MulitouchEntry.class);
        intentYes.putExtra("FROM_MULTITOUCH", "yes");
        intentYes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getIntent().getStringExtra(FROM) != null)
        {intentYes.putExtra(FROM, VOLUME);}
        startActivity(intentYes);
        setDefaults(MULTITOUCH, SUCCESS, TestMultitouch.this);
        handler.removeCallbacks(runnable);
    }
}
