package com.android.mobiledoctor;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.Manifest;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.CHARGING;
import static com.android.mobiledoctor.MainActivity.DIMMING;
import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestDimming extends AppCompatActivity {
SeekBar seekBar;
Button pass, fail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dimming);
        seekBar = findViewById(R.id.seekbar);
        pass = findViewById(R.id.pass);
        fail = findViewById(R.id.fail);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                dim(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(DIMMING, SUCCESS, TestDimming.this);
                Toast.makeText(TestDimming.this, "Test Passed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(DIMMING, FAILED, TestDimming.this);
                Toast.makeText(TestDimming.this, "Test Failed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

   private void dim(float range){
       float brightness = range / (float)10;
       WindowManager.LayoutParams lp = getWindow().getAttributes();
       lp.screenBrightness = brightness;
       getWindow().setAttributes(lp);
    }


}
