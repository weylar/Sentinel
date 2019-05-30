package com.android.mobiledoctor.HealthCheck.Connectivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.R;

import java.util.List;

import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.WIFI;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;

public class TestWIFI extends AppCompatActivity {

    WifiManager mWifiManager;
    ListView deviceName;
    Context context;
    TextView result, wifiStatus, listStatus, skip;
    Button move;
    ProgressBar progressBar, progressSearch;
    private static final int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wifi);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deviceName = findViewById(R.id.deviceName);
        move = findViewById(R.id.button);
        progressBar = findViewById(R.id.progress);
        progressBar.setMax(10);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        listStatus = findViewById(R.id.listStatus);
        wifiStatus = findViewById(R.id.wifiStatus);
        progressSearch = findViewById(R.id.progressSearch);
        context = this;

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(WIFI, UNCHECKED, context);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(WIFI, UNCHECKED, this);
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
        setDefaults(WIFI, UNCHECKED, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWifiManager.setWifiEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.setWifiEnabled(true);
        wifiStatus.setText("WI-FI Status - On");
        searchWifi();
        progressTimer();
        timer(120000, context);
    }

    private void progressTimer() {
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

                    progressBar.setProgress(i);


                }
            }
        };

        thread.start();
    }

    private void timer(long delayMillis, final Context context) {
        Runnable timerTask = new Runnable() {
            @Override
            public void run() {
                setFail(context);
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }

    private void setFail(Context context) {
        setDefaults(WIFI, FAILED, context);
        result.setText("FAIL");
        progressSearch.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        skip.setVisibility(View.GONE);
        move.setVisibility(View.VISIBLE);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void searchWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COURSE_LOCATION);
        } else {
            mWifiManager.startScan();
            statusCheck();
            progressSearch.setVisibility(View.VISIBLE);
        }

    }

    private class CustomAdapter extends ArrayAdapter<ScanResult> {
        List<ScanResult> scanResults;

        public CustomAdapter(Context context, List<ScanResult> scanResults) {
            super(context, android.R.layout.simple_list_item_1, scanResults);
            this.scanResults = scanResults;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            TextView tv = row.findViewById(android.R.id.text1);
            tv.setText(position + 1 + ". " + scanResults.get(position).SSID);
            return row;
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        showDialog();
    }

    private void showDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        customDialog.show();
    }

    public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

        Activity activity;
        Button yes, no;
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
            no = findViewById(R.id.no);
            heading = findViewById(R.id.heading);
            details = findViewById(R.id.details);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

            heading.setText("WI-FI");
            details.setText("Your GPS seems to be disabled, you need to enable it before searching for available WIFI");
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.yes:
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    break;
                case R.id.no:
                    cancel();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                progressSearch.setVisibility(View.GONE);
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                final CustomAdapter adapter = new CustomAdapter(TestWIFI.this, mScanResults);
                deviceName.setAdapter(adapter);
                if (adapter.getCount() < 1) {
                    listStatus.setVisibility(View.VISIBLE);
                    listStatus.setText("No Wi-Fi detected");
                } else {
                    listStatus.setVisibility(View.GONE);
                }

              deviceName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                     ScanResult item = adapter.getItem(i);
                      String networkSSID = item.SSID;

                      WifiConfiguration conf = new WifiConfiguration();
                      conf.SSID = "\"" + networkSSID + "\"";
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

                              result.setText("PASS");
                              result.setTextColor(getResources().getColor(R.color.green));
                              progressSearch.setVisibility(View.GONE);
                              progressBar.setVisibility(View.GONE);
                              setDefaults(WIFI, SUCCESS, context);
                              skip.setVisibility(View.GONE);
                              move.setVisibility(View.VISIBLE);
                              move.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View view) {
                                      finish();
                                  }
                              });

                              break;
                          }
                      }
                  }
              });
            }
        }
    };

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
