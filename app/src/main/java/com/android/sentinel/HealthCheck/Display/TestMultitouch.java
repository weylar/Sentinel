package com.android.sentinel.HealthCheck.Display;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.MULTITOUCH;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestMultitouch extends AppCompatActivity {
    Toast toast;
    View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multitouch);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        customView = getLayoutInflater().inflate(R.layout.sound_toast, (ViewGroup) findViewById(R.id.rel));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        if (index == 1) {
            vibrate();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toast = Toast.makeText(this, "Toast:Gravity.CENTER", Toast.LENGTH_SHORT);
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
        showToast();
    }

    private void showToast() {
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setView(customView);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toast.cancel();
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

    /*=====================Custom Dialog========================*/
    public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

        Activity activity;
        Button yes, no;

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
            no = findViewById(R.id.no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.yes:
                    Intent intentYes = new Intent(TestMultitouch.this, MulitouchEntry.class);
                    intentYes.putExtra("FROM_MULTITOUCH", "yes");
                    startActivity(intentYes);
                    setDefaults(MULTITOUCH, SUCCESS, TestMultitouch.this);
                    break;
                case R.id.no:
                    Intent intentNo = new Intent(TestMultitouch.this, MulitouchEntry.class);
                    intentNo.putExtra("FROM_MULTITOUCH", "yes");
                    startActivity(intentNo);
                    setDefaults(MULTITOUCH, FAILED, TestMultitouch.this);
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}
