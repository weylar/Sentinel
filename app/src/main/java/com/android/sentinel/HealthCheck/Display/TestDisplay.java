package com.android.sentinel.HealthCheck.Display;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.DISPLAY;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.MULTITOUCH;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.TOUCHSCREEN;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestDisplay extends AppCompatActivity {
    LinearLayout linearLayout;
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_display);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linearLayout = findViewById(R.id.linear);
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
    protected void onResume() {
        super.onResume();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == 0) {
                    linearLayout.setBackgroundColor(Color.BLACK);
                    state++;
                } else if (state == 1) {
                    linearLayout.setBackgroundColor(Color.RED);
                    state++;
                } else if (state == 2) {
                    linearLayout.setBackgroundColor(Color.BLUE);
                    state++;
                } else if (state == 3) {
                    linearLayout.setBackgroundColor(Color.YELLOW);
                    state++;
                } else if (state == 4) {
                    linearLayout.setBackgroundColor(Color.GREEN);
                    state++;
                } else if (state == 5) {
                    linearLayout.setBackgroundColor(Color.WHITE);
                    state = 0;
                    showDialog();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void showDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        customDialog.show();
    }

    public class CustomDialog extends Dialog implements android.view.View.OnClickListener {
        Activity activity;
        Button yes, no;
        TextView details, header;

        public CustomDialog(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            yes = findViewById(R.id.yes);
            details = findViewById(R.id.details);
            header = findViewById(R.id.heading);
            header.setText(getResources().getString(R.string.display_header));
            details.setText(getResources().getString(R.string.display_details));
            no = findViewById(R.id.no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.yes:
                    Intent intentYes = new Intent(TestDisplay.this, DisplayEntry.class);
                    intentYes.putExtra("FROM_DISPLAY", "yes");
                    intentYes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    if (getIntent().getStringExtra(FROM) != null)
                    {intentYes.putExtra(FROM, TOUCHSCREEN);}
                    startActivity(intentYes);
                    setDefaults(DISPLAY, SUCCESS, TestDisplay.this);
                    break;
                case R.id.no:
                    Intent intentNo = new Intent(TestDisplay.this, DisplayEntry.class);
                    intentNo.putExtra("FROM_DISPLAY", "yes");
                    intentNo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    if (getIntent().getStringExtra(FROM) != null)
                    {intentNo.putExtra(FROM, TOUCHSCREEN);}
                    startActivity(intentNo);
                    setDefaults(DISPLAY, FAILED, TestDisplay.this);
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}

