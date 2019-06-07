package com.android.sentinel.HealthCheck;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.Audio.TestEarphone;
import com.android.sentinel.HealthCheck.Audio.TestMicrophone;
import com.android.sentinel.HealthCheck.Audio.TestReceiver;
import com.android.sentinel.HealthCheck.Audio.TestSpeaker;
import com.android.sentinel.HealthCheck.Audio.TestVibration;
import com.android.sentinel.HealthCheck.Battery.TestBattery;
import com.android.sentinel.HealthCheck.Buttons.TestHomeButton;
import com.android.sentinel.HealthCheck.Buttons.TestPower;
import com.android.sentinel.HealthCheck.Buttons.TestVolume;
import com.android.sentinel.HealthCheck.Camera.PrimaryCamEntry;
import com.android.sentinel.HealthCheck.Camera.SecondaryCameraEntry;
import com.android.sentinel.HealthCheck.Camera.TestFlash;
import com.android.sentinel.HealthCheck.Connectivity.TestBluetooth;
import com.android.sentinel.HealthCheck.Connectivity.TestCellular;
import com.android.sentinel.HealthCheck.Connectivity.TestCharging;
import com.android.sentinel.HealthCheck.Connectivity.TestHeadphoneJack;
import com.android.sentinel.HealthCheck.Connectivity.TestWIFI;
import com.android.sentinel.HealthCheck.Display.DisplayEntry;
import com.android.sentinel.HealthCheck.Display.MulitouchEntry;
import com.android.sentinel.HealthCheck.Display.TestDimming;
import com.android.sentinel.HealthCheck.Display.TouchscreenEntry;
import com.android.sentinel.HealthCheck.Sensor.TestCompass;
import com.android.sentinel.HealthCheck.Sensor.TestFingerPrint;
import com.android.sentinel.HealthCheck.Sensor.TestSensor;
import com.android.sentinel.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class TestFragment extends Fragment {

    /*======================================================================*/
    public static final int UNCHECKED = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    /*=====================================================================*/
    public static final String MULTITOUCH = "multitouch";
    public static final String MIC = "mic";
    public static final String COMPASS = "compass";
    public static final String SENSOR = "sensor";
    public static final String FINGERPRINT = "fingerprint";
    public static final String DISPLAY = "display";
    public static final String CALL = "call";
    public static final String TOUCHSCREEN = "touchscreen";
    public static final String HEADPHONE = "headphone";
    public static final String EARPHONE = "earphone";
    public static final String SECONDARY_CAMERA = "secondary_camera";
    public static final String PRIMARY_CAMERA = "primary_camera";
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
    public static final String SPEAKER = "speaker";
    public static final String RECEIVER = "receiver";
    /*====================================================================*/
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String FROM = "from";
    List<String> groupBattery;
    List<String> groupDisplay;
    List<String> groupButton;
    List<String> groupCamera;
    List<String> groupAudio;
    List<String> groupConnectivity;
    List<String> groupSensor;
    List<LinkedHashMap<String, List<Integer>>> child;
    List<List<String>> group;
    ExpandableListView listView;
    ListAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        listView = view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        adapter = new ListAdapter(getActivity());
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                if (groupPosition == 0) {

                    if (childPosition == 0) {
                        Intent intentBatt = new Intent(getActivity(), TestBattery.class);
                        startActivity(intentBatt);
                    }

                } else if (groupPosition == 1) {

                    if (childPosition == 0) {
                        Intent intentTestMultiTouch = new Intent(getActivity(), MulitouchEntry.class);
                        startActivity(intentTestMultiTouch);
                    } else if (childPosition == 1) {
                        Intent intentTestTouch = new Intent(getActivity(), TouchscreenEntry.class);
                        startActivity(intentTestTouch);
                    } else if (childPosition == 2) {
                        Intent intentDisplay = new Intent(getActivity(), DisplayEntry.class);
                        startActivity(intentDisplay);
                    } else if (childPosition == 3) {
                        Intent intentDimming = new Intent(getActivity(), TestDimming.class);
                        startActivity(intentDimming);
                    }
                } else if (groupPosition == 2) {

                    if (childPosition == 0) {
                        Intent intentHome = new Intent(getActivity(), TestHomeButton.class);
                        startActivity(intentHome);
                    } else if (childPosition == 1) {
                        Intent intentVolume = new Intent(getActivity(), TestVolume.class);
                        startActivity(intentVolume);
                    } else if (childPosition == 2) {
                        Intent intentPower = new Intent(getActivity(), TestPower.class);
                        startActivity(intentPower);
                    }
                } else if (groupPosition == 3) {

                    if (childPosition == 0) {
                        Intent intentFrontCam = new Intent(getActivity(), SecondaryCameraEntry.class);
                        startActivity(intentFrontCam);
                    } else if (childPosition == 1) {
                        Intent intentFrontCam = new Intent(getActivity(), PrimaryCamEntry.class);
                        startActivity(intentFrontCam);
                    } else if (childPosition == 2) {
                        Intent intentFlash = new Intent(getActivity(), TestFlash.class);
                        startActivity(intentFlash);
                    }
                } else if (groupPosition == 4) {

                    if (childPosition == 0) {
                        Intent intentEarphone = new Intent(getActivity(), TestEarphone.class);
                        startActivity(intentEarphone);
                    } else if (childPosition == 1) {
                        Intent intentSpeaker = new Intent(getActivity(), TestSpeaker.class);
                        startActivity(intentSpeaker);
                    } else if (childPosition == 2) {
                        Intent intentReceiver = new Intent(getActivity(), TestReceiver.class);
                        startActivity(intentReceiver);
                    } else if (childPosition == 3) {
                        Intent intentMic = new Intent(getActivity(), TestMicrophone.class);
                        startActivity(intentMic);
                    } else if (childPosition == 4) {
                        Intent intentVibration = new Intent(getActivity(), TestVibration.class);
                        startActivity(intentVibration);
                    }
                } else if (groupPosition == 5) {

                    if (childPosition == 0) {
                        Intent intentCellular = new Intent(getActivity(), TestCellular.class);
                        startActivity(intentCellular);
                    } else if (childPosition == 1) {
                        Intent intentWIFI = new Intent(getActivity(), TestWIFI.class);
                        startActivity(intentWIFI);
                    } else if (childPosition == 2) {
                        Intent intentBluetooth = new Intent(getActivity(), TestBluetooth.class);
                        startActivity(intentBluetooth);
                    } else if (childPosition == 3) {
                        Intent intentHeadphoneJack = new Intent(getActivity(), TestHeadphoneJack.class);
                        startActivity(intentHeadphoneJack);
                    } else if (childPosition == 4) {
                        Intent intentCharge = new Intent(getActivity(), TestCharging.class);
                        startActivity(intentCharge);
                    }
                } else if (groupPosition == 6) {

                    if (childPosition == 0) {
                        Intent intentCompass = new Intent(getActivity(), TestCompass.class);
                        startActivity(intentCompass);
                    } else if (childPosition == 1) {
                        Intent intentSensor = new Intent(getActivity(), TestSensor.class);
                        startActivity(intentSensor);
                    } else if (childPosition == 2) {
                        Intent testFingerprint = new Intent(getActivity(), TestFingerPrint.class);
                        startActivity(testFingerprint);
                    }
                }
                return true;
            }
        });
    }



    public List<LinkedHashMap<String, List<Integer>>> getData() {
        group = new ArrayList<>();
        groupBattery = new ArrayList<>();
        groupBattery.add("Battery");
        groupBattery.add("Battery and Power");
        groupBattery.add("1 test");


        groupDisplay = new ArrayList<>();
        groupDisplay.add("Display");
        groupDisplay.add("Display and Touchscreen");
        groupDisplay.add("4 tests");

        groupAudio = new ArrayList<>();
        groupAudio.add("Audio");
        groupAudio.add("Microphones and Speakers");
        groupAudio.add("5 tests");

        groupConnectivity = new ArrayList<>();
        groupConnectivity.add("Connectivity");
        groupConnectivity.add("Networks and Connections");
        groupConnectivity.add("5 tests");

        groupCamera = new ArrayList<>();
        groupCamera.add("Camera");
        groupCamera.add("Camera Hardware and Flash");
        groupCamera.add("3 tests");

        groupButton = new ArrayList<>();
        groupButton.add("Buttons");
        groupButton.add("Buttons and Controls");
        groupButton.add("3 tests");


        groupSensor = new ArrayList<>();
        groupSensor.add("Sensors");
        groupSensor.add("Motion and other Sensors");
        groupSensor.add("3 tests");

        group.add(groupBattery);
        group.add(groupDisplay);
        group.add(groupButton);
        group.add(groupCamera);
        group.add(groupAudio);
        group.add(groupConnectivity);
        group.add(groupSensor);


        LinkedHashMap<String, List<Integer>> childBattery = new LinkedHashMap<>();
        List<Integer> extra = new ArrayList<>();
        extra.add(R.drawable.battery_100px);
        extra.add(getDefaults(BATTERY, getActivity()));
        childBattery.put("Battery Health", extra);
        //childBattery.put("Wireless Charge", R.drawable.audio_100px);


        LinkedHashMap<String, List<Integer>> childDisplay = new LinkedHashMap<>();
        List<Integer> extraMultitouch = new ArrayList<>();
        extraMultitouch.add(R.drawable.multitouch_50px);
        extraMultitouch.add(getDefaults(MULTITOUCH, getActivity()));

        List<Integer> extraTouchscreen = new ArrayList<>();
        extraTouchscreen.add(R.drawable.touch_50px);
        extraTouchscreen.add(getDefaults(TOUCHSCREEN, getActivity()));

        List<Integer> extraDisplay = new ArrayList<>();
        extraDisplay.add(R.drawable.display_50px);
        extraDisplay.add(getDefaults(DISPLAY, getActivity()));

        List<Integer> extraDimming = new ArrayList<>();
        extraDimming.add(R.drawable.dim_50px);
        extraDimming.add(getDefaults(DIMMING, getActivity()));

        childDisplay.put("Multitouch", extraMultitouch);
        childDisplay.put("Touchscreen", extraTouchscreen);
        childDisplay.put("Display", extraDisplay);
        childDisplay.put("Screen Dimming", extraDimming);


        LinkedHashMap<String, List<Integer>> childAudio = new LinkedHashMap<>();

        List<Integer> extraEarphone = new ArrayList<>();
        extraEarphone.add(R.drawable.earphone_50px);
        extraEarphone.add(getDefaults(EARPHONE, getActivity()));

        List<Integer> extraSpeaker = new ArrayList<>();
        extraSpeaker.add(R.drawable.speaker_50px);
        extraSpeaker.add(getDefaults(SPEAKER, getActivity()));

        List<Integer> extraReceiver = new ArrayList<>();
        extraReceiver.add(R.drawable.receiver_50px);
        extraReceiver.add(getDefaults(RECEIVER, getActivity()));

        List<Integer> extraMic = new ArrayList<>();
        extraMic.add(R.drawable.mic_50px);
        extraMic.add(getDefaults(MIC, getActivity()));

        List<Integer> extraVibrator = new ArrayList<>();
        extraVibrator.add(R.drawable.vibrate_50px);
        extraVibrator.add(getDefaults(VIBRATOR, getActivity()));

        childAudio.put("Earphone", extraEarphone);
        childAudio.put("Speaker", extraSpeaker);
        childAudio.put("Receiver", extraReceiver);
        childAudio.put("Microphone", extraMic);
        childAudio.put("Vibrator", extraVibrator);


        LinkedHashMap<String, List<Integer>> childConnectivity = new LinkedHashMap<>();
        List<Integer> extraCall = new ArrayList<>();
        extraCall.add(R.drawable.call_50px);
        extraCall.add(getDefaults(CALL, getActivity()));

        List<Integer> extraNetwork = new ArrayList<>();
        extraNetwork.add(R.drawable.network_50px);
        extraNetwork.add(getDefaults(NETWORK, getActivity()));

        List<Integer> extraWifi = new ArrayList<>();
        extraWifi.add(R.drawable.wifi_50px);
        extraWifi.add(getDefaults(WIFI, getActivity()));

        List<Integer> extraBluetooth = new ArrayList<>();
        extraBluetooth.add(R.drawable.bluetooth_50px);
        extraBluetooth.add(getDefaults(BLUETOOTH, getActivity()));

        List<Integer> extraHeadphone = new ArrayList<>();
        extraHeadphone.add(R.drawable.port_50px);
        extraHeadphone.add(getDefaults(HEADPHONE, getActivity()));

        List<Integer> extraCharging = new ArrayList<>();
        extraCharging.add(R.drawable.charge_50px);
        extraCharging.add(getDefaults(CHARGING, getActivity()));


        // childConnectivity.put("Call Function", extraCall);
        childConnectivity.put("Cellular Network", extraNetwork);
        childConnectivity.put("WIFI Connection", extraWifi);
        childConnectivity.put("Bluetooth", extraBluetooth);
        childConnectivity.put("Headphone Jack", extraHeadphone);
        childConnectivity.put("Charging Port", extraCharging);

        LinkedHashMap<String, List<Integer>> childCamera = new LinkedHashMap<>();
        List<Integer> extraSecondaryCamera = new ArrayList<>();
        extraSecondaryCamera.add(R.drawable.secondary_cam_50px);
        extraSecondaryCamera.add(getDefaults(SECONDARY_CAMERA, getActivity()));

        List<Integer> extraPrimaryCamera = new ArrayList<>();
        extraPrimaryCamera.add(R.drawable.primary_cam_50px);
        extraPrimaryCamera.add(getDefaults(PRIMARY_CAMERA, getActivity()));

        List<Integer> extraFlash = new ArrayList<>();
        extraFlash.add(R.drawable.flashlight_50px);
        extraFlash.add(getDefaults(FLASH, getActivity()));

        childCamera.put("Secondary Camera", extraSecondaryCamera);
        childCamera.put("Primary Camera", extraPrimaryCamera);
        childCamera.put("Flash Light", extraFlash);


        LinkedHashMap<String, List<Integer>> childButtons = new LinkedHashMap<>();
        List<Integer> extraHome = new ArrayList<>();
        extraHome.add(R.drawable.home_50px);
        extraHome.add(getDefaults(HOME, getActivity()));

        List<Integer> extraVolume = new ArrayList<>();
        extraVolume.add(R.drawable.volume_50px);
        extraVolume.add(getDefaults(VOLUME, getActivity()));

        List<Integer> extraPower = new ArrayList<>();
        extraPower.add(R.drawable.power_50px);
        extraPower.add(getDefaults(POWER, getActivity()));

        childButtons.put("Home Button", extraHome);
        childButtons.put("Volume Control", extraVolume);
        childButtons.put("Power Button", extraPower);


        LinkedHashMap<String, List<Integer>> childSensor = new LinkedHashMap<>();
        List<Integer> extraCompass = new ArrayList<>();
        extraCompass.add(R.drawable.compass_50px);
        extraCompass.add(getDefaults(COMPASS, getActivity()));

        List<Integer> extraSensor = new ArrayList<>();
        extraSensor.add(R.drawable.sensor_50px);
        extraSensor.add(getDefaults(SENSOR, getActivity()));

        List<Integer> extraFingerprint = new ArrayList<>();
        extraFingerprint.add(R.drawable.fingerprint_50px);
        extraFingerprint.add(getDefaults(FINGERPRINT, getActivity()));

        childSensor.put("Compass", extraCompass);
        childSensor.put("Sensor", extraSensor);
        childSensor.put("Fingerprint", extraFingerprint);

        child = new ArrayList<>();
        child.add(childBattery);
        child.add(childDisplay);
        child.add(childButtons);
        child.add(childCamera);
        child.add(childAudio);
        child.add(childConnectivity);
        child.add(childSensor);

        return child;
    }

    /*I created this methods setDefault and getDefault as a global static
     method to get and set sharedPreferences to track state of results*/
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

    class ListAdapter extends BaseExpandableListAdapter {
        private Context context;


        ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return group.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return child.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return group.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return child.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_header, null);
            }
            TextView header = view.findViewById(R.id.titleHeader);
            TextView extra = view.findViewById(R.id.headerDescription);
            TextView testNumber = view.findViewById(R.id.testNumber);
            ImageView imageView = view.findViewById(R.id.imageHeader);
            header.setText(group.get(i).get(0));
            extra.setText(group.get(i).get(1));
            testNumber.setText(group.get(i).get(2));
            switch (group.get(i).get(0)) {
                case "Battery":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.battery_100px));
                    break;
                case "Display":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.display_100px));
                    break;
                case "Buttons":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.button_100px));
                    break;
                case "Camera":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.camera_100px));
                    break;
                case "Audio":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.audio_100px));
                    break;
                case "Connectivity":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.connectivity_100px));
                    break;
                case "Sensors":
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.sensor_100px));
                    break;
            }

            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_children, null);
            }

            TextView title = view.findViewById(R.id.titleChild);
            ImageView image = view.findViewById(R.id.imageChild);
            ImageView status = view.findViewById(R.id.status);
            title.setText(new ArrayList<>(child.get(groupPosition).keySet()).get(childPosition));
            image.setImageResource(new ArrayList<>(child.get(groupPosition).values()).get(childPosition).get(0));

            switch (new ArrayList<>(child.get(groupPosition).values()).get(childPosition).get(1)) {
                case 0:
                    status.setVisibility(View.GONE);
                    break;
                case 1:
                    status.setImageResource(R.drawable.passed);
                    break;
                case 2:
                    status.setImageResource(R.drawable.failed);
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }



}
