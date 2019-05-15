package com.android.mobiledoctor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public static final String MY_SHARED_PREFERENCE = "com.android.mobiledoctor.aminu.idris";
    /*======================================================================*/
    public static final int UNCHECKED = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    /*=====================================================================*/
    public static final String TOUCHSCREEN = "touchscreen";
    public static final String HEADPHONE = "headphone";
    public static final String CAMERA = "camera";
    public static final String FLASH = "flash";
    public static final String HOME = "home";
    public static final String VOLUME = "volume";
    public static final String POWER = "power";
    public static final String STORAGE = "storage";
    public static final String MEMORY = "memory";
    public static final String VIBRATOR = "vibrator";
    public static final String SPECIFICATION = "specification";
    public static final String NETWORK = "network";
    public static final String WIFI = "wifi";
    public static final String BLUETOOTH = "bluetooth";
    public static final String BATTERY = "battery";
    public static final String DIMMING = "dimming";
    public static final String CHARGING = "charging";
    /*====================================================================*/
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int[] states = {getDefaults(TOUCHSCREEN, this), getDefaults(HEADPHONE, this),
                getDefaults(CAMERA, this), getDefaults(FLASH, this),
                getDefaults(HOME, this), getDefaults(VOLUME, this),
                getDefaults(POWER, this), getDefaults(STORAGE, this),
                getDefaults(MEMORY, this), getDefaults(VIBRATOR, this),
                getDefaults(SPECIFICATION, this), getDefaults(NETWORK, this),
                getDefaults(WIFI, this), getDefaults(BLUETOOTH, this),
                getDefaults(BATTERY, this), getDefaults(DIMMING, this), getDefaults(CHARGING, this)};

        String[] itemToTest = {"Touchscreen", "Headphone Jack", "Camera",
                "Flash", "Home Button", "Volume Control", "Power Button",
                "Storage", "Memory", "Vibrator", "Phone Specification",
                "Cellular Network", "Wifi Connection", "Bluetooth",
                "Battery", "Screen Dimming", "Charging Port"};

        ListView listView = findViewById(R.id.listView);
        MainAdapter adapter = new MainAdapter(this, itemToTest, states);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent intentTestTouch = new Intent(MainActivity.this, TestTouch.class);
                        startActivity(intentTestTouch);
                        break;
                    case 1:
                        Intent intentHeadphoneJack = new Intent(MainActivity.this, TestHeadphoneJack.class);
                        startActivity(intentHeadphoneJack);
                        break;
                    case 2:
                        Intent intentFrontCam = new Intent(MainActivity.this, TestCamera.class);
                        startActivity(intentFrontCam);
                        break;
                    case 3:
                        Intent intentFlash = new Intent(MainActivity.this, TestFlash.class);
                        startActivity(intentFlash);
                        break;
                    case 4:
                        Intent intentHome = new Intent(MainActivity.this, TestHomeButton.class);
                        startActivity(intentHome);
                        break;
                    case 5:
                        Intent intentVolume = new Intent(MainActivity.this, TestVolume.class);
                        startActivity(intentVolume);
                        break;
                    case 6:
                        Intent intentPower = new Intent(MainActivity.this, TestPower.class);
                        startActivity(intentPower);
                        break;
                    case 7:
                        Intent intentStorage = new Intent(MainActivity.this, TestStorage.class);
                        startActivity(intentStorage);
                        break;
                    case 8:
                        Intent intentMemory = new Intent(MainActivity.this, TestMemory.class);
                        startActivity(intentMemory);
                        break;
                    case 9:
                        Intent intentVibration = new Intent(MainActivity.this, TestVibration.class);
                        startActivity(intentVibration);
                        break;
                    case 10:
                        Intent intentSpecification = new Intent(MainActivity.this, TestSpecification.class);
                        startActivity(intentSpecification);
                        break;
                    case 11:
                        Intent intentCellular = new Intent(MainActivity.this, TestCellular.class);
                        startActivity(intentCellular);
                        break;
                    case 12:
                        Intent intentWIFI = new Intent(MainActivity.this, TestWIFI.class);
                        startActivity(intentWIFI);
                        break;
                    case 13:
                        Intent intentBluetooth = new Intent(MainActivity.this, TestBluetooth.class);
                        startActivity(intentBluetooth);
                        break;
                    case 14:
                        Intent intentBatt = new Intent(MainActivity.this, TestBattery.class);
                        startActivity(intentBatt);
                        break;
                    case 15:
                        Intent intentDimming = new Intent(MainActivity.this, TestDimming.class);
                        startActivity(intentDimming);
                        break;
                    case 16:
                        Intent intentCharge = new Intent(MainActivity.this, TestCharging.class);
                        startActivity(intentCharge);
                        break;

                }
            }
        });
    }

    /*I created this methods setDefault and getDefault as a global static
     method to get and set sharedPrefences to track state of checks*/
    public static void setDefaults(String key, int value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, UNCHECKED);
    }

    class MainAdapter extends ArrayAdapter<String> {
        String[] itemToTest;
        int[] checkState;


        MainAdapter(Context context, String[] value, int[] checkState) {
            super(context, R.layout.customlist, value);
            this.itemToTest = value;
            this.checkState = checkState;

        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.customlist, parent, false);

            TextView tv = row.findViewById(R.id.text);
            RelativeLayout relativeLayout = row.findViewById(R.id.color);
            TextView value = row.findViewById(R.id.value);
            if (checkState[pos] == SUCCESS) {
                relativeLayout.setBackgroundColor(Color.parseColor("green"));
                value.setText("Passed");
            } else if (checkState[pos] == FAILED) {
                relativeLayout.setBackgroundColor(Color.parseColor("red"));
                value.setText("Failed");
            }

            tv.setText(itemToTest[pos]);
            return row;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
