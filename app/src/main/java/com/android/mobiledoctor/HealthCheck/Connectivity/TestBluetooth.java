package com.android.mobiledoctor.HealthCheck.Connectivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.R;

import static com.android.mobiledoctor.HealthCheck.TestFragment.BLUETOOTH;
import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.MULTITOUCH;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;

public class TestBluetooth extends AppCompatActivity {
    TextView result, skip;
    ProgressBar progressBar;
    Button move;
    ProgressBar progressBarSearch;
    BluetoothAdapter mBluetoothAdapter;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    Context context;
    Runnable timerTask;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bluetooth);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progress);
        result = findViewById(R.id.result);
        move = findViewById(R.id.move);
        skip = findViewById(R.id.skip);
        progressBarSearch = findViewById(R.id.progressSearch);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        context = this;
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(BLUETOOTH, UNCHECKED, context);
                finish();
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(BLUETOOTH, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
         handler = new Handler();
        runProgressBar();
        timer(120000, context);
        onBluetooth();
        searchDevice();
    }

    private void timer(long delayMillis, final Context context) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                setFail(context);

            }
        };
        handler.postDelayed(timerTask, delayMillis);
    }

    private void setFail(Context context) {
        setDefaults(BLUETOOTH, FAILED, context);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        move.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressBar.setVisibility(View.GONE);
        progressBarSearch.setVisibility(View.GONE);
    }

    private void onBluetooth() {
        if (mBluetoothAdapter == null) {
            showDialog();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }

        }
    }

    private void showDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        customDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
    }

    private void runProgressBar() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= 10; i++) {
                    if (i > 0) {
                        try {
                            sleep(12000);
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

    private void searchDevice() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted Idris show dialog here
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        }
    }

    public class CustomDialog extends Dialog implements android.view.View.OnClickListener {
        Activity activity;
        Button yes;
        TextView details, heading;

        public CustomDialog(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            yes = findViewById(R.id.yes);
            yes.setText("Exit");
            heading = findViewById(R.id.heading);
            details = findViewById(R.id.details);
            yes.setOnClickListener(this);
            heading.setText("Not compatible");
            details.setText("Your phone does not support Bluetooth");
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.yes:
                    cancel();
                    break;

            }
            dismiss();
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                progressBarSearch.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismiss progress dialog
                progressBarSearch.setVisibility(View.GONE);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                TextView deviceName = findViewById(R.id.deviceName);
                setPass(context, device, deviceName);
                if (device.getName().equals("")){
                    deviceName.setText("No Devices Found");
                }

            }
        }
    };

    private void setPass(Context context, BluetoothDevice device, TextView deviceName) {
        deviceName.setVisibility(View.VISIBLE);
        deviceName.setText("Name: " + device.getName() + "\n" + "Address: " + device.getAddress());
        setDefaults(BLUETOOTH, SUCCESS, context);
        result.setVisibility(View.VISIBLE);
        result.setText("Pass");
        result.setTextColor(getResources().getColor(R.color.green));
        move.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        handler.removeCallbacks(timerTask);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
