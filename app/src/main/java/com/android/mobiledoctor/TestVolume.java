package com.android.mobiledoctor;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.HOME;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.TOUCHSCREEN;
import static com.android.mobiledoctor.MainActivity.VOLUME;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestVolume extends AppCompatActivity {
int check;
    Runnable timerTask;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_volume);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer(15000, this);
        check = 0;

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timerTask);
    }

    private void timer(long delayMillis, final Context context) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                setDefaults(VOLUME, FAILED, context);
                Toast.makeText(context, "Sorry! Test Failed!", Toast.LENGTH_SHORT).show();
                finish();

            }
        };
        handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
           /* case KeyEvent.KEYCODE_MENU:
                Toast.makeText(this, "Menu key pressed", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                Toast.makeText(this, "Search key pressed", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;*/
            case KeyEvent.KEYCODE_VOLUME_UP:
                Toast.makeText(this,"Volume Up pressed", Toast.LENGTH_SHORT).show();
                check += 1;
                checkPress();
                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Toast.makeText(this,"Volume Down pressed", Toast.LENGTH_SHORT).show();
                check += 1;
                checkPress();
                return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkPress() {
        if (check == 2) {
            setDefaults(VOLUME, SUCCESS, this);
            Toast.makeText(this, "Woo! Test Passed!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
