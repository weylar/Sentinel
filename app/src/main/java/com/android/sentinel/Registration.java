package com.android.sentinel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import static com.android.sentinel.HealthCheck.DeviceInfoFragment.getTotalInternalMemorySize;

public class Registration extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_PHONE = 1;
    EditText deviceName, storageSize, deviceImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();

        deviceName = findViewById(R.id.deviceName);
        storageSize = findViewById(R.id.storageSize);
        deviceImei = findViewById(R.id.deviceImei);

        deviceName.setText(Build.BRAND + " " + Build.MODEL);
        storageSize.setText(getTotalInternalMemorySize());
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_PHONE);
        } else {
            deviceImei.setText(telephonyManager.getDeviceId());
        }


    }

    public void login(View view) {
        startActivity(new Intent(this, Login.class));
    }
}
