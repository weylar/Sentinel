package com.android.mobiledoctor.HealthCheck.Display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.R;

import static com.android.mobiledoctor.HealthCheck.TestFragment.TOUCHSCREEN;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.getDefaults;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;

public class TouchscreenEntry extends AppCompatActivity {
    TextView result, skip, explanation;
    Button btn;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchscreen_entry);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        btn = findViewById(R.id.button);
        explanation = findViewById(R.id.explain);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(TOUCHSCREEN, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (value != null && value.equals("yes")) {
            if (getDefaults(TOUCHSCREEN, this) == 1) {
                Intent intent = new Intent(TouchscreenEntry.this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                setDefaults(TOUCHSCREEN, UNCHECKED, this);
            } else if (getDefaults(TOUCHSCREEN, this) == 2) {
                Intent intent = new Intent(TouchscreenEntry.this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                setDefaults(TOUCHSCREEN, UNCHECKED, this);
            }
        } else {
            super.onBackPressed();
            setDefaults(TOUCHSCREEN, UNCHECKED, this);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        value = getIntent().getStringExtra("FROM_TOUCH");
        if (value != null && value.equals("yes")) {
            switch (getDefaults(TOUCHSCREEN, this)) {
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
                    Intent intent = new Intent(TouchscreenEntry.this, TestTouch.class);
                    startActivity(intent);
                }
            });

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaults(TOUCHSCREEN, UNCHECKED, TouchscreenEntry.this);
                    finish();
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
                Intent intent = new Intent(TouchscreenEntry.this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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
                Intent intent = new Intent(TouchscreenEntry.this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

}
