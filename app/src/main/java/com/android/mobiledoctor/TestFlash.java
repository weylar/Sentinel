package com.android.mobiledoctor;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.FLASH;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.TOUCHSCREEN;
import static com.android.mobiledoctor.MainActivity.setDefaults;


public class TestFlash extends AppCompatActivity {
    Context context;
    static Camera cam = null;
    Button pass;
    Button fail;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_flash);
        context = TestFlash.this;
        pass = findViewById(R.id.pass);
        fail = findViewById(R.id.fail);

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FLASH, SUCCESS, context);
                Toast.makeText(context, "Woo! Test Passed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(FLASH, FAILED, context);
                Toast.makeText(context, "Sorry! Test Failed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        /*Trigger flash as activity ius created*/
        onFlash();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            offFlash();
        }
    }


    private void onFlash() {
//        Check for device permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
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
                }
            } catch (Exception e) {
                Log.e("Error", "" + e);
            }
        }
    }

    //    Result to be sent back after appropriate permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                            cam = Camera.open();
                            Camera.Parameters p = cam.getParameters();
                            p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                            cam.setParameters(p);
                            cam.startPreview();
                        } else {
                            Toast.makeText(context, "Phone does not have a flash", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("Error", "" + e);
                    }
                } else {
                    Toast.makeText(context, "Permission was denied ", Toast.LENGTH_SHORT).show();
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

}
