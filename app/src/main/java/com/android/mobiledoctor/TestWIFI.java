package com.android.mobiledoctor;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.NETWORK;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.WIFI;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestWIFI extends AppCompatActivity {
Switch aSwitch;
WifiManager mWifiManager;
ListView deviceName;
Context context;
ProgressBar progressBar;
private static final int  MY_PERMISSIONS_REQUEST_COURSE_LOCATION  = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wifi);
        aSwitch = findViewById(R.id.idSwitch);
        deviceName = findViewById(R.id.deviceName);
        progressBar = findViewById(R.id.progress);
        context = this;

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if (mWifiManager.isWifiEnabled()){
            aSwitch.setChecked(true);
        }else{
            aSwitch.setChecked(false);
        }

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aSwitch.isChecked()){
                    WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                }else{
                    WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(false);
                }
            }
        });
    }

    public void searchWifi(View v){
       // Check for device permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COURSE_LOCATION);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            mWifiManager.startScan();
            statusCheck();



        }

    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, you need to enable it before searching for available WIFI")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                progressBar.setVisibility(View.GONE);
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                final ArrayAdapter<ScanResult> adapter = new ArrayAdapter<>(TestWIFI.this, android.R.layout.simple_list_item_1, mScanResults);
                deviceName.setAdapter(adapter);

              deviceName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                     ScanResult item = adapter.getItem(i);
                      String networkSSID = item.SSID;
                      String networkPass = "pass";

                      WifiConfiguration conf = new WifiConfiguration();
                      conf.SSID = "\"" + networkSSID + "\"";

                      /*For Wep Network*/
//                      conf.wepKeys[0] = "\"" + networkPass + "\"";
//                      conf.wepTxKeyIndex = 0;
//                      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//                      conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

                      /*For WPA Network*/
//                      conf.preSharedKey = "\""+ networkPass +"\"";

                      //For open Network
                      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                      /*Connect to android device*/
                      WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                      wifiManager.addNetwork(conf);
                      List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                      for( WifiConfiguration b : list ) {
                          if(b.SSID != null && b.SSID.equals("\"" + networkSSID + "\"")) {
                              wifiManager.disconnect();
                              wifiManager.enableNetwork(b.networkId, true);
                              wifiManager.reconnect();

                              Toast.makeText(context, "Test Passed!", Toast.LENGTH_SHORT).show();
                              setDefaults(WIFI, SUCCESS, context);
                              finish();
                              break;
                          }
                      }
                  }
              });
            }
        }
    };

    public void Pass (View view){
        setDefaults(WIFI, SUCCESS, context);
        Toast.makeText(context, "Woo! Test Passed!", Toast.LENGTH_SHORT).show();
        finish();
    }
    public void Fail(View view){
        setDefaults(WIFI, FAILED, context);
        Toast.makeText(context, "Test Failed!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COURSE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    progressBar.setVisibility(View.VISIBLE);
                    mWifiManager.startScan();
                } else {
                    Toast.makeText(context, "Permission was denied ", Toast.LENGTH_SHORT).show();
                }
                break;



        }
        }


}
