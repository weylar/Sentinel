package com.android.sentinel.HealthCheck.Connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.NETWORK;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestCellular extends AppCompatActivity {
    TextView result, reason;
    ProgressBar progressBar;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    Thread thread;
    Button move;
    NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cellular);
        result = findViewById(R.id.result);
        reason = findViewById(R.id.reason);
        move = findViewById(R.id.move);
        progressBar = findViewById(R.id.progress);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(NETWORK, UNCHECKED, this);
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
        setDefaults(NETWORK, UNCHECKED, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    public void move(View view) {
        finish();
    }
//    private void timer(long delayMillis) {
//        Runnable timerTask = new Runnable() {
//            @Override
//            public void run() {
//               setFail(TestCellular.this);
//            }
//        };
//        Handler handler = new Handler();
//        handler.postDelayed(timerTask, delayMillis);
//    }
    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()
                        && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    setPass(context);
                } else {
                    setFail(context);
                    }
                }

            }
        }

    private void setFail(final Context context) {
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        reason.setVisibility(View.VISIBLE);
        reason.setText("Are you sure your network is switched on. If not, put in on, it will automatically update");
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(NETWORK, FAILED, context);
                finish();
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void setPass(final Context context) {
        result.setText("PASS");
        result.setTextColor(getResources().getColor(R.color.green));
        reason.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(NETWORK, SUCCESS, context);
                finish();
            }
        });
        setDefaults(NETWORK, SUCCESS, context);
    }

}
