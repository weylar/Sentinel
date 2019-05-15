package com.android.mobiledoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.CHARGING;
import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.HEADPHONE;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.TOUCHSCREEN;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestHeadphoneJack extends AppCompatActivity {
TextView textView;
Runnable timerTask;
Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_headphone_jack);
        textView = findViewById(R.id.text);



    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetStateReceiver receiver = new HeadsetStateReceiver();
        registerReceiver( receiver, receiverFilter );
    }

    private void timer(long delayMillis, final Context context) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                setDefaults(HEADPHONE, FAILED, context);
                Toast.makeText(context, "Sorry! Test Failed!", Toast.LENGTH_SHORT).show();
                finish();

            }
        };
        handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(timerTask);

    }

    public class HeadsetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if ((action.equals(Intent.ACTION_HEADSET_PLUG)))
            {
                int headSetState = intent.getIntExtra("state", 0);
                if (headSetState == 0) {
                    textView.setText("No headphone detected, please insert headphone ");
                    timer(20000, context);

                }else{
                    setDefaults(HEADPHONE, SUCCESS, TestHeadphoneJack.this);
                    Toast.makeText(context, "Headphone detected, test passed!", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        }
        }

}
