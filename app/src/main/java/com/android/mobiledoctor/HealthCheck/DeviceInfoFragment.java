package com.android.mobiledoctor.HealthCheck;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.mobiledoctor.R;
import java.io.File;
import static com.android.mobiledoctor.HealthCheck.TestFragment.BATTERY;
import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;
import static java.util.jar.Pack200.Packer.ERROR;

public class DeviceInfoFragment extends Fragment {
    TextView battHealth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_info, container, false);
        showSpecification(view);
        showStorage(view);
        showMemory(view);
        showBattery(view);

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

    private void showStorage(View view) {
        ProgressBar progressBar = view.findViewById(R.id.progressStorage);
        TextView textViewSize = view.findViewById(R.id.size);
        TextView textViewAvailable = view.findViewById(R.id.available);
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        Long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        long megAvailable = bytesAvailable / (1024 * 1024);
        megAvailable /= 1024;
        textViewAvailable.setText(megAvailable + " GB Available");
        textViewSize.setText("Total size: " + getTotalInternalMemorySize());
        progressBar.setMax((int) getTotalInternalMemorySizeInt());
        progressBar.setProgress((int) getTotalInternalMemorySizeInt() - (int) megAvailable);


    }

    public static boolean isExternalMemoryAvailable() {
        return android.os.Environment.
                getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
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

    public static String getTotalExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.
                    getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long BlockSize = stat.getBlockSizeLong();
            long TotalBlocks = stat.getBlockCountLong();
            return formatSize(TotalBlocks * BlockSize);
        } else {
            return ERROR;
        }
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
