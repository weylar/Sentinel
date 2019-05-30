package com.android.mobiledoctor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lusfold.spinnerloading.SpinnerLoading;

public class SplashScreen extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable runnable;
    SpinnerLoading spinnerLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        timer(3000);
    }

    private void timer(long timeInMilli) {
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Home.class);
                startActivity(intent);

            }

        };

        handler.postDelayed(runnable, timeInMilli);
    }
}
