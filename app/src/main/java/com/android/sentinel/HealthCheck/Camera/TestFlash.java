package com.android.sentinel.HealthCheck.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.Audio.TestSpeaker;
import com.android.sentinel.HealthCheck.Display.TestDimming;
import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.DIMMING;
import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FLASH;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.SPEAKER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;


public class TestFlash extends AppCompatActivity {
    Context context;
    static Camera cam = null;
    Button pass, fail;
    TextView skip;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_flash);
        context = TestFlash.this;
        pass = findViewById(R.id.pass);
        fail = findViewById(R.id.fail);
        skip = findViewById(R.id.skip);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FLASH, SUCCESS, context);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(DIMMING)) {
                        Intent intent = new Intent(TestFlash.this, TestSpeaker.class);
                        intent.putExtra(FROM, FLASH);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });
        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FLASH, FAILED, context);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(DIMMING)) {
                        Intent intent = new Intent(TestFlash.this, TestSpeaker.class);
                        intent.putExtra(FROM, FLASH);
                        startActivity(intent);
                    }
                }else {
                    finish();
                }
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FLASH, UNCHECKED, context);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(DIMMING)) {
                        Intent intent = new Intent(TestFlash.this, TestSpeaker.class);
                        intent.putExtra(FROM, FLASH);
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
                setDefaults(FLASH, UNCHECKED, this);
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
        setDefaults(FLASH, UNCHECKED, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            offFlash();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onFlash();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                            cam = Camera.open();
                            Camera.Parameters p = cam.getParameters();
                            p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                            cam.setParameters(p);
                            cam.startPreview();
                        } else {
                            Toast.makeText(context, "Phone does not have a flash", Toast.LENGTH_LONG).show();
                            setDefaults(FLASH, UNCHECKED, context);
                            if (getIntent().getExtras() != null) {
                                String val = getIntent().getStringExtra(FROM);
                                if (val.equals(DIMMING)) {
                                    Intent intent = new Intent(TestFlash.this, TestSpeaker.class);
                                    intent.putExtra(FROM, FLASH);
                                    startActivity(intent);
                                }
                            }else {
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Error", "" + e);
                    }
                } else {
                    Toast.makeText(context, "Permission was denied ", Toast.LENGTH_SHORT).show();
                    setDefaults(FLASH, UNCHECKED, context);
                    if (getIntent().getExtras() != null) {
                        String val = getIntent().getStringExtra(FROM);
                        if (val.equals(DIMMING)) {
                            Intent intent = new Intent(TestFlash.this, TestSpeaker.class);
                            intent.putExtra(FROM, FLASH);
                            startActivity(intent);
                        }
                    }else {
                        finish();
                    }
                }
                return;
            }
        }
    }

    private void offFlash() {
        cam.stopPreview();
        cam.release();
        cam = null;
    }

    private void onFlash() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            try {
                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    cam = Camera.open();
                    Camera.Parameters p = cam.getParameters();
                    p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    cam.startPreview();
                } else {
                    Toast.makeText(context, "Phone does not have a flash", Toast.LENGTH_LONG).show();
                    setDefaults(FLASH, UNCHECKED, context);
                    if (getIntent().getExtras() != null) {
                        String val = getIntent().getStringExtra(FROM);
                        if (val.equals(DIMMING)) {
                            Intent intent = new Intent(TestFlash.this, TestSpeaker.class);
                            intent.putExtra(FROM, FLASH);
                            startActivity(intent);
                        }
                    }else {
                        finish();
                    }
                }
            } catch (Exception e) {
                Log.e("Error", "" + e);
            }
        }
    }
}
