package com.android.mobiledoctor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

public class Value extends AppCompatActivity {
DonutProgress donutProgress;
TextView qualify, price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);

        donutProgress =  findViewById(R.id.progress);
        donutProgress.setProgress(40);

        qualify = findViewById(R.id.qualify);
        price = findViewById(R.id.price);

        qualify.setText("Qualified for Trade-in");
        price.setText("Device Value is \u20A6" + "10,000" );


    }
}
