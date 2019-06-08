package com.android.sentinel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.github.lzyzsd.circleprogress.DonutProgress;

import static com.android.sentinel.HealthCheck.TestFragment.BATTERY;
import static com.android.sentinel.HealthCheck.TestFragment.BLUETOOTH;
import static com.android.sentinel.HealthCheck.TestFragment.CHARGING;
import static com.android.sentinel.HealthCheck.TestFragment.COMPASS;
import static com.android.sentinel.HealthCheck.TestFragment.DIMMING;
import static com.android.sentinel.HealthCheck.TestFragment.DISPLAY;
import static com.android.sentinel.HealthCheck.TestFragment.EARPHONE;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FINGERPRINT;
import static com.android.sentinel.HealthCheck.TestFragment.FLASH;
import static com.android.sentinel.HealthCheck.TestFragment.HEADPHONE;
import static com.android.sentinel.HealthCheck.TestFragment.HOME;
import static com.android.sentinel.HealthCheck.TestFragment.MIC;
import static com.android.sentinel.HealthCheck.TestFragment.MULTITOUCH;
import static com.android.sentinel.HealthCheck.TestFragment.NETWORK;
import static com.android.sentinel.HealthCheck.TestFragment.POWER;
import static com.android.sentinel.HealthCheck.TestFragment.PRIMARY_CAMERA;
import static com.android.sentinel.HealthCheck.TestFragment.RECEIVER;
import static com.android.sentinel.HealthCheck.TestFragment.SECONDARY_CAMERA;
import static com.android.sentinel.HealthCheck.TestFragment.SENSOR;
import static com.android.sentinel.HealthCheck.TestFragment.SPEAKER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.TOUCHSCREEN;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.VIBRATOR;
import static com.android.sentinel.HealthCheck.TestFragment.VOLUME;
import static com.android.sentinel.HealthCheck.TestFragment.WIFI;
import static com.android.sentinel.HealthCheck.TestFragment.getDefaults;

public class HealthResult extends AppCompatActivity {
    DonutProgress donutProgress;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_result);
        donutProgress = findViewById(R.id.progress);
        donutProgress.setProgress((int)calcPercentage());
        listView = findViewById(R.id.listview);
        ResultAdapter resultAdapter = new ResultAdapter(this,
                getResults(), getTestNames(), getIcon());
        listView.setAdapter(resultAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HealthCheck.class));
    }

    /*Super magical method that calculates result percentage*/
    private float calcPercentage(){
        return  ((float)calculateResult()/getResults().length) * 100;

    }



    /*My magical Method that calculates my result based on values gotten, Bolade you can use this too*/
    public int  calculateResult(){
        int result = 0;
        for (int i = 0; i < getResults().length; i++){
            if (getResults()[i] == SUCCESS){
            result++;
            }

        }
        return result;
    }

    /*This is an open method that returns result state of all test with SUCCESS, FAILED and UNCHECKED int values , 1, 2 and 0 respectively*/
    private int[] getResults() {
        int[] result = {getDefaults(BATTERY, this),
                getDefaults(DIMMING, this),
                getDefaults(FLASH, this),
                getDefaults(SPEAKER, this),
                getDefaults(RECEIVER, this),
                getDefaults(MIC, this),
                getDefaults(EARPHONE, this),
                getDefaults(VIBRATOR, this),
                getDefaults(NETWORK, this),
                getDefaults(HEADPHONE, this),
                getDefaults(CHARGING, this),
                getDefaults(COMPASS, this),
                getDefaults(SENSOR, this),
                getDefaults(FINGERPRINT, this),
                getDefaults(SECONDARY_CAMERA, this),
                getDefaults(PRIMARY_CAMERA, this),
                getDefaults(POWER, this),
                getDefaults(HOME, this),
                getDefaults(VOLUME, this),
                getDefaults(MULTITOUCH, this),
                getDefaults(TOUCHSCREEN, this),
                getDefaults(DISPLAY, this),
                getDefaults(BLUETOOTH, this),
                getDefaults(WIFI, this)
        };

        return result;
    }
    private String[] getTestNames() {
        String[] testName = {"Battery",
                "Screen Dimming",
                "Flashlight",
                "Speaker",
                "Receiver",
                "Microphone",
                "Earphone",
                "Vibrator",
                "Cellular Network",
                "Headphone Jack",
                "Charging Port",
                "Compass",
                "Sensor",
                "Fingerprint",
                "Secondary Camera",
                "Primary Camera",
                "Power Button",
                "Home Button",
                "Volume",
                "Multitouch",
                "Touchscreen",
                "Display",
                "Bluetooth",
                "Wifi Connection"
        };

        return testName;
    }
    private int[] getIcon() {
        int[] icons = {R.drawable.battery_50px,
                R.drawable.dim_50px,
                R.drawable.flashlight_50px,
                R.drawable.speaker_50px,
                R.drawable.receiver_50px,
                R.drawable.mic_50px,
                R.drawable.earphone_50px,
                R.drawable.vibrate_50px,
                R.drawable.network_50px,
                R.drawable.port_50px,
                R.drawable.charge_50px,
                R.drawable.compass_50px,
                R.drawable.sensor_50px,
                R.drawable.fingerprint_50px,
                R.drawable.secondary_cam_50px,
                R.drawable.primary_cam_50px,
                R.drawable.power_50px,
                R.drawable.home_50px,
                R.drawable.volume_50px,
                R.drawable.multitouch_50px,
                R.drawable.touch_50px,
                R.drawable.display_50px,
                R.drawable.bluetooth_50px,
                R.drawable.wifi_50px
        };

        return icons;
    }

    private class ResultAdapter extends ArrayAdapter<String> {
        int[] results;
        int[] icons;
        String[] testNames;

        public ResultAdapter(Context context, int[] results, String[] testNames, int[] icons) {
            super(context, R.layout.result_list_view, testNames);
            this.results = results;
            this.testNames = testNames;
            this.icons = icons;
        }

        @NonNull
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.result_list_view, parent, false);

            ImageView icon = view.findViewById(R.id.icon);
            ImageView check = view.findViewById(R.id.check);
            TextView title = view.findViewById(R.id.title);

            icon.setImageResource(icons[pos]);
            title.setText(testNames[pos]);
           switch (results[pos]){
               case UNCHECKED:
                   check.setImageDrawable(getResources().getDrawable(R.drawable.unchecked_50px));
                   break;
               case SUCCESS:
                   check.setImageDrawable(getResources().getDrawable(R.drawable.passed));
                   break;
               case FAILED:
                   check.setImageDrawable(getResources().getDrawable(R.drawable.failed));
                   break;
           }

            return view;
        }
    }
}
