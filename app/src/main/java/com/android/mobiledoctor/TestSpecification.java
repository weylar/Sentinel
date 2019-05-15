package com.android.mobiledoctor;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import static com.android.mobiledoctor.MainActivity.SPECIFICATION;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestSpecification extends AppCompatActivity {
TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_specification);
        textView = findViewById(R.id.text);

        //Show Device specification
        textView.append("Model: " + Build.MODEL + "\n");
        textView.append("Serial: " + Build.SERIAL + "\n");
        textView.append("ID: " + Build.ID + "\n");
        textView.append("Manufacturer: " + Build.MANUFACTURER + "\n");
        textView.append("Brand: " + Build.BRAND + "\n");
        textView.append("Type: " + Build.TYPE + "\n");
        textView.append("User: " + Build.USER + "\n");
        textView.append("Base: " + Build.VERSION_CODES.BASE + "\n");
        textView.append("Incremental: " + Build.VERSION.INCREMENTAL + "\n");
        textView.append("SDK: " + Build.VERSION.SDK + "\n");
        textView.append("Board: " + Build.BOARD + "\n");
        textView.append("Brand: " + Build.BRAND + "\n");
        textView.append("Host: " + Build.HOST + "\n");
        textView.append("Fingerprint: " + Build.FINGERPRINT + "\n");
        textView.append("Android Version: " + Build.VERSION.RELEASE + "\n");

        setDefaults(SPECIFICATION, SUCCESS, this);


    }
}
