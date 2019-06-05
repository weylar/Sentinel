package com.android.mobiledoctor.HealthCheck;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobiledoctor.R;

import java.io.File;

import static com.android.mobiledoctor.HealthCheck.TestFragment.BATTERY;
import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;
import static java.util.jar.Pack200.Packer.ERROR;

public class DeviceInfoFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_PHONE = 1;
    TextView battHealth;
    View view;
    TextView phoneState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.device_info, container, false);
        phoneState = view.findViewById(R.id.phone_state);
        showSpecification(view);
        showStorage(view);
        showMemory(view);
        showBattery(view);
        getPhoneState(view);

        return view;
    }

    private void showBattery(View view) {
        battHealth = view.findViewById(R.id.battery);
        battHealth.append("(" + showBatPercentage() + "%)");
        isCharging receiver = new isCharging();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(receiver, ifilter);
    }

    public int showBatPercentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }

    public class isCharging extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            if (status == BatteryManager.BATTERY_HEALTH_COLD) {
                battHealth.setText("Battery Health - Cold");
                setDefaults(BATTERY, FAILED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_DEAD) {
                battHealth.setText("Battery Health - Dead");
                setDefaults(BATTERY, FAILED, context);
            }
            if (status == BatteryManager.BATTERY_HEALTH_GOOD) {
                battHealth.setText("Battery Health - Good");
                setDefaults(BATTERY, SUCCESS, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                battHealth.setText("Battery Health - Overheat");
                setDefaults(BATTERY, FAILED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                battHealth.setText("Battery Health - Over Voltage");
                setDefaults(BATTERY, FAILED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_UNKNOWN) {
                battHealth.setText("Battery Health - Unknown");
                setDefaults(BATTERY, UNCHECKED, context);

            }
            if (status == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                battHealth.setText("Battery Health - Unspecified Failure");
                setDefaults(BATTERY, FAILED, context);

            }
        }


    }

    private void showMemory(View view) {
        TextView ram = view.findViewById(R.id.ram);
        ImageView imgRam = view.findViewById(R.id.imgRam);
        ImageView imgProcessor = view.findViewById(R.id.imgProcessor);
        ImageView imgMemClass = view.findViewById(R.id.imgMemClass);
        TextView processor = view.findViewById(R.id.processor);
        TextView memClass = view.findViewById(R.id.memClass);
        if (isRamOk()) {
            ram.setText("Ram Performance: High");
            imgRam.setImageDrawable(getResources().getDrawable(R.drawable.passed));
        } else {
            ram.setText("Ram Performance: Low");
            imgRam.setImageDrawable(getResources().getDrawable(R.drawable.failed));
        }
        if (isProcessorOk()) {
            processor.setText("Processor: High");
            imgProcessor.setImageDrawable(getResources().getDrawable(R.drawable.passed));
        } else {
            processor.setText("Processor: Low");
            imgProcessor.setImageDrawable(getResources().getDrawable(R.drawable.failed));
        }
        if (isMemClassOk()) {
            memClass.setText("Memory Class: High");
            imgMemClass.setImageDrawable(getResources().getDrawable(R.drawable.passed));
        } else {
            memClass.setText("Memory Class: Low");
            imgMemClass.setImageDrawable(getResources().getDrawable(R.drawable.failed));

        }
    }

    private boolean isRamOk() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        return !activityManager.isLowRamDevice();
    }

    private boolean isProcessorOk() {
        return Runtime.getRuntime().availableProcessors() >= 4;
    }

    private boolean isMemClassOk() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass() >= 128;
    }

    private void showSpecification(View view) {
        TextView textView = view.findViewById(R.id.specification);
        //Show Device specification
        textView.append("Model: " + Build.MODEL + "\n");
        textView.append("Android Version: " + Build.VERSION.RELEASE + "\n");
        textView.append("ID: " + Build.ID + "\n");
        textView.append("Serial: " + Build.SERIAL + "\n");
        textView.append("Brand: " + Build.BRAND + "\n");
        textView.append("Type: " + Build.TYPE + "\n");
        textView.append("User: " + Build.USER + "\n");
        textView.append("Base: " + Build.VERSION_CODES.BASE + "\n");
        textView.append("Manufacturer: " + Build.MANUFACTURER + "\n");
        textView.append("SDK: " + Build.VERSION.SDK + "\n");
        textView.append("Board: " + Build.BOARD + "\n");
        textView.append("Brand: " + Build.BRAND + "\n");
        textView.append("Host: " + Build.HOST + "\n");
        textView.append("Incremental: " + Build.VERSION.INCREMENTAL + "\n");
        textView.append("Fingerprint: " + Build.FINGERPRINT + "\n");

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhoneState(view);
                } else {
                    Toast.makeText(getActivity(), "Permission was denied ", Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
                return;
            }
        }
    }

    private void getPhoneState(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_PHONE);
        } else {
            String phoneTypeString = "";
            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            int phoneType = telephonyManager.getPhoneType();
            switch (phoneType) {
                case (TelephonyManager.PHONE_TYPE_CDMA):
                    phoneTypeString = "CDMA";
                    break;
                case (TelephonyManager.PHONE_TYPE_GSM):
                    phoneTypeString = "GSM";
                    break;
                case (TelephonyManager.PHONE_TYPE_NONE):
                    phoneTypeString = "NONE";
                    break;
            }
            boolean isRoaming = telephonyManager.isNetworkRoaming();
            phoneState.append("Phone Network Type: " + phoneTypeString +  "\n");
            phoneState.append("IMEI Number: " + telephonyManager.getDeviceId() +  "\n");
            phoneState.append("Subscriber ID: " + telephonyManager.getDeviceId() +  "\n");
            phoneState.append("Sim Serial Number: " + telephonyManager.getSimSerialNumber() +  "\n");
            phoneState.append("Network Country ISO: " + telephonyManager.getNetworkCountryIso() +  "\n");
            phoneState.append("Sim Country ISO: " + telephonyManager.getSimCountryIso() +  "\n");
            phoneState.append("Voice Mail Number: " + telephonyManager.getVoiceMailNumber() +  "\n");
            phoneState.append("In Roaming: " + isRoaming);

        }
    }


    private void showStorage(View view) {
        ProgressBar progressBar = view.findViewById(R.id.progressStorage);
        TextView textViewSize = view.findViewById(R.id.size);
        TextView textViewAvailable = view.findViewById(R.id.available);
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        Long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        long megAvailable = bytesAvailable;
        textViewAvailable.setText("Avail: " + formatSize(megAvailable));
        textViewSize.setText("Total: " + getTotalInternalMemorySize());
        progressBar.setMax((int) getTotalInternalMemorySizeInt());
        progressBar.setProgress((int) getTotalInternalMemorySizeInt() - (int) formatSizeInt(megAvailable));


    }



    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long BlockSize = stat.getBlockSizeLong();
        long TotalBlocks = stat.getBlockCountLong();
        return formatSize(TotalBlocks * BlockSize);
    }

    public static long getTotalInternalMemorySizeInt() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long BlockSize = stat.getBlockSizeLong();
        long TotalBlocks = stat.getBlockCountLong();
        return formatSizeInt(TotalBlocks * BlockSize);
    }



    public static long formatSizeInt(long size) {
        if (size >= 1024) {
            size /= 1024;
            if (size >= 1024) {
                size /= 1024;
                if (size >= 1024) {
                    size /= 1024;
                    if (size >= 1024) {
                        size /= 1024;
                    }
                }
            }
        }
        return size;
    }

    public static String formatSize(long size) {
        String suffixSize = null;

        if (size >= 1024) {
            suffixSize = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffixSize = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffixSize = "GB";
                    size /= 1024;
                    if (size >= 1024) {
                        suffixSize = "TB";
                        size /= 1024;
                    }
                }
            }
        }

        StringBuilder BufferSize = new StringBuilder(
                Long.toString(size));

        int commaOffset = BufferSize.length() - 3;
        while (commaOffset > 0) {
            BufferSize.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffixSize != null) BufferSize.append(suffixSize);
        return BufferSize.toString();
    }

}
