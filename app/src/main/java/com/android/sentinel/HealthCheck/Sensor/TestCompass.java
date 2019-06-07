package com.android.sentinel.HealthCheck.Sensor;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.Connectivity.TestCharging;
import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.CHARGING;
import static com.android.sentinel.HealthCheck.TestFragment.COMPASS;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.HEADPHONE;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestCompass extends AppCompatActivity implements SensorEventListener {
    TextView compassState, compassStateDetail, result, skip;
    Button move;
    ImageView imageView;
    SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_compass);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        compassState = findViewById(R.id.compass_state);
        compassStateDetail = findViewById(R.id.compass_state_details);
        imageView = findViewById(R.id.image);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        move = findViewById(R.id.move);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(COMPASS, UNCHECKED, TestCompass.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(CHARGING)) {
                        Intent intent = new Intent(TestCompass.this, TestSensor.class);
                        intent.putExtra(FROM, COMPASS);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(COMPASS, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setDefaults(COMPASS, UNCHECKED, this);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            ra.setDuration(250);
            ra.setFillAfter(true);

            imageView.startAnimation(ra);
            compassState.setVisibility(View.VISIBLE);
            compassStateDetail.setVisibility(View.VISIBLE);
            compassState.setText("Heading: " + (int) azimuthInDegress + " degrees");
            compassStateDetail.setText("Radian: " + (int) azimuthInRadians);
            mCurrentDegree = -azimuthInDegress;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);


    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void passAction(View view) {
        setDefaults(COMPASS, SUCCESS, this);
        if (getIntent().getExtras() != null) {
            String val = getIntent().getStringExtra(FROM);
            if (val.equals(CHARGING)) {
                Intent intent = new Intent(TestCompass.this, TestSensor.class);
                intent.putExtra(FROM, COMPASS);
                startActivity(intent);
            }
        }else {
            finish();
        }

    }

    public void failAction(View view) {
        setDefaults(COMPASS, FAILED, this);
        if (getIntent().getExtras() != null) {
            String val = getIntent().getStringExtra(FROM);
            if (val.equals(CHARGING)) {
                Intent intent = new Intent(TestCompass.this, TestSensor.class);
                intent.putExtra(FROM, COMPASS);
                startActivity(intent);
            }
        }else {
            finish();
        }
    }


}
