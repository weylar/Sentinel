package com.android.mobiledoctor;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.TOUCHSCREEN;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestTouch extends AppCompatActivity {
    LinearLayout linearLayout;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_touch);
        linearLayout = findViewById(R.id.linear);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();


        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*Getting the touch coordinates brother Bolade*/
                int X = (int) event.getX();
                int Y = (int) event.getY();


               if (toast != null)
                   toast.cancel();
               else {
                   toast = Toast.makeText(TestTouch.this, "X coordinates: " + X + "\nY coordinates: " + Y, Toast.LENGTH_SHORT);
                   toast.show();
                   toast = null;
               }
                vibrate();
                return false;
            }
        });
    }


    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }

    @Override
    public void onBackPressed() {
        showDialog();

    }

    private void showDialog (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Touch Screen");
        builder.setMessage("Did all part respond to touch?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(TestTouch.this, "Woo! Test Passed!", Toast.LENGTH_SHORT).show();
                setDefaults(TOUCHSCREEN, SUCCESS, TestTouch.this);
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(TestTouch.this, "Sorry! Test Failed!", Toast.LENGTH_SHORT).show();
                setDefaults(TOUCHSCREEN, FAILED, TestTouch.this);
                finish();
            }
        });
        builder.create().show();
    }
}
