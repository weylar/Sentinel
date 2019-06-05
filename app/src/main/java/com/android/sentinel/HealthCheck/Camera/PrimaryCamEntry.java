package com.android.sentinel.HealthCheck.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.PRIMARY_CAMERA;
import static com.android.sentinel.HealthCheck.TestFragment.REQUEST_IMAGE_CAPTURE;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class PrimaryCamEntry extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    TextView result, skip, explanation;
    Context context = this;
    Button btn, pass, fail;
    ImageView imageView;
    LinearLayout responseKeys;
    Intent takePictureIntent;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_cam_entry);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.preview);
        cardView = findViewById(R.id.cardView);
        skip = findViewById(R.id.skip);
        btn = findViewById(R.id.button);
        pass = findViewById(R.id.pass);
        fail = findViewById(R.id.fail);
        responseKeys = findViewById(R.id.responseKeys);
        explanation = findViewById(R.id.explain);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(PRIMARY_CAMERA, UNCHECKED, context);
                finish();
            }
        });
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(PRIMARY_CAMERA, UNCHECKED, this);
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
        setDefaults(PRIMARY_CAMERA, UNCHECKED, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            responseKeys.setVisibility(View.VISIBLE);
            skip.setVisibility(View.GONE);
            explanation.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(imageBitmap);
            pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaults(PRIMARY_CAMERA, SUCCESS, context);
                    finish();
                }
            });
            fail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDefaults(PRIMARY_CAMERA, FAILED, context);
                    finish();
                }
            });
        } else if (resultCode == RESULT_CANCELED) {
            setDefaults(PRIMARY_CAMERA, UNCHECKED, context);
            Intent intent = new Intent(PrimaryCamEntry.this, HealthCheck.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(context, "Permission was denied ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            try {
                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(context, "Phone does not have a camera", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("Error", "" + e);
            }
        }
    }

}
