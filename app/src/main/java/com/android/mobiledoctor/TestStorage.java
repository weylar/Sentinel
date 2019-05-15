package com.android.mobiledoctor;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import static com.android.mobiledoctor.MainActivity.MEMORY;
import static com.android.mobiledoctor.MainActivity.STORAGE;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;
import static java.util.jar.Pack200.Packer.ERROR;

public class TestStorage extends AppCompatActivity {
TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_storage);
        textView = findViewById(R.id.text);
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

        Long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();

        long megAvailable = bytesAvailable / (1024 * 1024);
        megAvailable /= 1024;
        textView.setText("(Available MB : " + megAvailable + "GB / " + getTotalInternalMemorySize() + ")");

    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefaults(STORAGE, SUCCESS, this);
        timer(5000, this);
    }

    private void timer(long delayMillis, final Context context) {
        Runnable timerTask = new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = findViewById(R.id.progress);
                TextView textView2 = findViewById(R.id.progressText);
                textView2.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);


            }
        };
        Handler handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }
    public static boolean isExternalMemoryAvailable() {
        return android.os.Environment.
                getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /*Get internal storage size*/
    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long BlockSize = stat.getBlockSizeLong();
        long TotalBlocks = stat.getBlockCountLong();
        return formatSize(TotalBlocks * BlockSize);
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

    /*Format Sizes*/
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



