package com.android.sentinel.HealthCheck.Display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.Connectivity.TestBluetooth;
import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.DISPLAY;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.MULTITOUCH;
import static com.android.sentinel.HealthCheck.TestFragment.TOUCHSCREEN;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.getDefaults;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class DisplayEntry extends AppCompatActivity {
    TextView result, skip, explanation;
    Button btn;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_entry);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        btn = findViewById(R.id.button);
        explanation = findViewById(R.id.explain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(DISPLAY, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra(FROM) != null){
            super.onBackPressed();
        }else {
            if (value != null && value.equals("yes")) {
                if (getDefaults(DISPLAY, this) == 1) {
                    Intent intent = new Intent(DisplayEntry.this, HealthCheck.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    setDefaults(DISPLAY, UNCHECKED, this);
                } else if (getDefaults(DISPLAY, this) == 2) {
                    Intent intent = new Intent(DisplayEntry.this, HealthCheck.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    setDefaults(DISPLAY, UNCHECKED, this);
                }
            } else {
                super.onBackPressed();
                setDefaults(DISPLAY, UNCHECKED, this);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        value = getIntent().getStringExtra("FROM_DISPLAY");
        if (value != null && value.equals("yes")) {
            switch (getDefaults(DISPLAY, this)) {
                case 1:
                    setPass();
                    break;
                case 2:
                    setFail();
                    break;
            }
        } else {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getIntent().getExtras() != null) {
                        String val = getIntent().getStringExtra(FROM);
                        if (val.equals(TOUCHSCREEN)) {
                            Intent intent = new Intent(DisplayEntry.this,
                                    TestDisplay.class);
                            intent.putExtra(FROM, TOUCHSCREEN);
                            startActivity(intent);
                        }
                    }else {
                        Intent intent = new Intent(DisplayEntry.this, TestDisplay.class);
                        startActivity(intent);
                    }
                }
            });

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaults(DISPLAY, UNCHECKED, DisplayEntry.this);
                    if (getIntent().getStringExtra(FROM) != null) {
                        String val = getIntent().getStringExtra(FROM);
                        if (val.equals(TOUCHSCREEN)) {
                            Intent intent = new Intent(DisplayEntry.this,
                                    TestBluetooth.class);
                            intent.putExtra(FROM, DISPLAY);
                            startActivity(intent);
                        }
                    }else {
                        finish();
                    }
                }
            });
        }


    }

    private void setFail() {
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        btn.setText("Continue");
        skip.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra(FROM) != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(TOUCHSCREEN)) {
                        Intent intent = new Intent(DisplayEntry.this,
                                TestBluetooth.class);
                        intent.putExtra(FROM, DISPLAY);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });
    }

    private void setPass() {
        result.setText("PASS");
        btn.setText("Continue");
        skip.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra(FROM) != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(TOUCHSCREEN)) {
                        Intent intent = new Intent(DisplayEntry.this,
                                TestBluetooth.class);
                        intent.putExtra(FROM, DISPLAY);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });
    }


}
