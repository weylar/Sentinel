package com.android.sentinel.HealthCheck.Audio;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.MIC;
import static com.android.sentinel.HealthCheck.TestFragment.RECEIVER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestMicrophone extends AppCompatActivity {

    TextView skip, insertEarpiece, explain, recorder, result;
    Button start;
    IntentFilter receiverFilter;
    HeadsetStateReceiver receiver;
    Handler handler;
    AudioRecord audioRecord = null;
    int minSize;
    Runnable timerTask;
    Runnable stopRecordingRunnable;
    ProgressBar progressBar;
    int isWorking = 2;
    AudioManager audioManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_microphone);
        getSupportActionBar().setHomeButtonEnabled(true);
        handler = new Handler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        skip = findViewById(R.id.skip);
        result = findViewById(R.id.result);
        recorder = findViewById(R.id.recorder);
        progressBar = findViewById(R.id.progress);
        explain = findViewById(R.id.explain);
        insertEarpiece = findViewById(R.id.insertEarpiece);
        start = findViewById(R.id.button);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(MIC, UNCHECKED, TestMicrophone.this);
                isWorking = 0;
                handler.removeCallbacks(timerTask);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(RECEIVER)) {
                        Intent intent = new Intent(TestMicrophone.this, TestEarphone.class);
                        intent.putExtra(FROM, MIC);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(MIC, UNCHECKED, this);
                handler.removeCallbacks(timerTask);
                isWorking = 0;
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
        setDefaults(MIC, UNCHECKED, this);
        handler.removeCallbacks(timerTask);
        isWorking = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        receiver = new HeadsetStateReceiver();
        registerReceiver(receiver, receiverFilter);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()){
            warnRemoveHeadphone();

        }else{
            start.setVisibility(View.VISIBLE);
            insertEarpiece.setVisibility(View.GONE);
            explain.setVisibility(View.VISIBLE);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermission();
                }
            });
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording();
                } else {
                    Toast.makeText(this, "Permission was denied ", Toast.LENGTH_SHORT).show();
                    if (getIntent().getExtras() != null) {
                        String val = getIntent().getStringExtra(FROM);
                        if (val.equals(RECEIVER)) {
                            Intent intent = new Intent(TestMicrophone.this, TestEarphone.class);
                            intent.putExtra(FROM, MIC);
                            startActivity(intent);
                        }
                    } else {
                        finish();
                    }
                }
                return;
            }
        }
    }

    private void warnRemoveHeadphone() {
        insertEarpiece.setVisibility(View.VISIBLE);
        insertEarpiece.setText(getResources().getString(R.string.remove_headphone));
        start.setVisibility(View.GONE);
        skip.setVisibility(View.VISIBLE);
        explain.setVisibility(View.GONE);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        } else {
            startRecording();

        }
    }

    private void startRecording() {
        minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minSize);
        audioRecord.startRecording();
        start.setVisibility(View.GONE);
        insertEarpiece.setVisibility(View.GONE);
        updateAmplitude(200);
        autoStopRecording(15000);
        updateProgress(1500, 10);

    }

    private void updateAmplitude(final long delayMillis) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    explain.setVisibility(View.VISIBLE);
                    explain.setText(getResources().getString(R.string.recorder));
                    recorder.setVisibility(View.VISIBLE);
                    recorder.setText("Amplitude - " + (int) getAmplitude());
                    if ((int) getAmplitude() < 1000) {
                        insertEarpiece.setVisibility(View.VISIBLE);
                        recorder.setTextColor(getResources().getColor(R.color.black));
                        insertEarpiece.setText("Input is low, please increase your voice");

                        insertEarpiece.setTextColor(getResources().getColor(R.color.colorPrimary));

                    } else {
                        insertEarpiece.setVisibility(View.GONE);
                        recorder.setTextColor(getResources().getColor(R.color.green));
                        isWorking = 1;


                    }

                } finally {
                    handler.postDelayed(timerTask, delayMillis);
                }


            }
        };

        timerTask.run();
    }

    private void autoStopRecording(final long millisec) {
        stopRecordingRunnable = new Runnable() {
            @Override
            public void run() {
                stopRecording();
            }
        };
        handler.postDelayed(stopRecordingRunnable, millisec);
    }

    public void updateProgress(final long millisec, final int max) {
        progressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i <= max; i++) {
                    if (i > 0) {
                        try {
                            sleep(millisec);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    progressBar.setMax(max);
                    progressBar.setProgress(i);
                }
            }
        };
        thread.start();
    }

    private void stopRecording() {
        if (audioRecord != null) {
            audioRecord.stop();
        }

        if (isWorking == 1) {
            result.setVisibility(View.VISIBLE);
            result.setText("PASS");
            result.setTextColor(getResources().getColor(R.color.green));
            setDefaults(MIC, SUCCESS, TestMicrophone.this);

        } else if (isWorking == 2) {
            result.setVisibility(View.VISIBLE);
            result.setText("FAIL");
            result.setTextColor(getResources().getColor(R.color.colorPrimary));
            setDefaults(MIC, FAILED, TestMicrophone.this);
        }


        handler.removeCallbacks(timerTask);
        insertEarpiece.setVisibility(View.GONE);
        recorder.setVisibility(View.GONE);
        explain.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        start.setText("Continue");
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(RECEIVER)) {
                        Intent intent = new Intent(TestMicrophone.this, TestEarphone.class);
                        intent.putExtra(FROM, MIC);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });


    }

    private double getAmplitude() {
        short[] buffer = new short[minSize];
        audioRecord.read(buffer, 0, minSize);
        int max = 0;
        for (short s : buffer) {
            if (Math.abs(s) > max) {
                max = Math.abs(s);
            }
        }
        return max;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
        handler.removeCallbacks(timerTask);
        handler.removeCallbacks(stopRecordingRunnable);
        unregisterReceiver(receiver);
    }

    public class HeadsetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if ((action.equals(Intent.ACTION_HEADSET_PLUG))) {
                int headSetState = intent.getIntExtra("state", 0);
                if (headSetState == 0) {
                    start.setVisibility(View.VISIBLE);
                    insertEarpiece.setVisibility(View.GONE);
                    explain.setVisibility(View.VISIBLE);
                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermission();
                        }
                    });

                } else {
                    warnRemoveHeadphone();

                }
            }
        }
    }


}
