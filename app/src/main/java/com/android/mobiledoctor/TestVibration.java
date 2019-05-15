package com.android.mobiledoctor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.MY_SHARED_PREFERENCE;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.VIBRATOR;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestVibration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_vibration);

    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(2000);
        }
    }

    public void Pass(View view){
        Toast.makeText(this, "Test Passed!", Toast.LENGTH_SHORT).show();
        setDefaults(VIBRATOR, SUCCESS, this);
        finish();
    }

    public void Fail(View view){
        Toast.makeText(this, "Fail Passed!", Toast.LENGTH_SHORT).show();
        setDefaults(VIBRATOR, FAILED, this);
        finish();
    }

    public void vibrate(View view){
        vibrate();
    }
}
