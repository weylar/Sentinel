package com.android.mobiledoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.MEMORY;
import static com.android.mobiledoctor.MainActivity.NETWORK;
import static com.android.mobiledoctor.MainActivity.POWER;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestCellular extends AppCompatActivity {
    TextView text;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cellular);
        text = findViewById(R.id.text);
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);

    }

    private void timer(long delayMillis, final Context context) {
        Runnable timerTask = new Runnable() {
            @Override
            public void run() {
                setDefaults(NETWORK, FAILED, context);
                Toast.makeText(context, "Sorry! Test Failed!", Toast.LENGTH_SHORT).show();
                finish();

            }
        };
        Handler handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()
                        && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        text.setText("Mobile Data Connectivity Test: Passed");
                    setDefaults(NETWORK, SUCCESS, context);
                    } else {
                        text.setText("Mobile Data Connectivity Test: Failed," +
                                " are you sure your network is switched on. " +
                                "If not put in on, it will automatically update here");
                    timer(20000, context);
                    }
                }

            }
        }

    }
