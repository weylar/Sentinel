package com.android.mobiledoctor;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.BLUETOOTH;
import static com.android.mobiledoctor.MainActivity.NETWORK;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestBluetooth extends AppCompatActivity {
    Switch aSwitch;
    Button search;
    ProgressBar progressBar;
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_BLUETOOTH = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCAATION = 1;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bluetooth);
        aSwitch = findViewById(R.id.idSwitch);
        search = findViewById(R.id.search);
        progressBar = findViewById(R.id.progress);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        context = TestBluetooth.this;

        //Chek if phone has bluetooth or not
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            /*Auto check switch if bluetooth is on*/
            if (mBluetoothAdapter.isEnabled()) {
                aSwitch.setChecked(true);
            } else {
                aSwitch.setChecked(false);
            }

            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (aSwitch.isChecked()) {
                        if (!mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.enable();
                        }
                    } else {

                        if (mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.disable();
                        }
                    }
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //        Check for device permission
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted Idris show dislog here
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCAATION);

                    } else {
                        searchDevice();
                    }

                }
            });


        }
    }

    private void searchDevice() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismiss progress dialog
                progressBar.setVisibility(View.GONE);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context, "Bluetooth Device Found, TEST PASSED!" , Toast.LENGTH_SHORT).show();
                TextView deviceName = findViewById(R.id.deviceName);
                deviceName.setText("Name: " + device.getName() + "\n" + "Address: " + device.getAddress());
                setDefaults(BLUETOOTH, SUCCESS, context);
                Toast.makeText(context, "Woo! Test Passed", Toast.LENGTH_SHORT).show();
                finish();
                if (device.getName().equals("")){
                    deviceName.setText("No Devices Found");
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
    }
}
