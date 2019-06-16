package com.android.sentinel.HealthCheck.Audio;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import java.io.IOException;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.MIC;
import static com.android.sentinel.HealthCheck.TestFragment.RECEIVER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestMicrophone extends AppCompatActivity {

    TextView skip, insertEarpiece, explain, recorder, result;
    Button start, pass, fail;
    IntentFilter receiverFilter;
    HeadsetStateReceiver receiver;
    Handler handler;
    Runnable timerTask;
    Runnable stopRecordingRunnable;
    ProgressBar progressBar;
    int isWorking = 2;
    AudioManager audioManager;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    String fileName = null;
    LinearLayout passFail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_microphone);
        getSupportActionBar().setHomeButtonEnabled(true);
        handler = new Handler();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        skip = findViewById(R.id.skip);
        pass = findViewById(R.id.pass);
        fail = findViewById(R.id.fail);
        passFail = findViewById(R.id.pass_fail);
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
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/AudioRecording.3gp";
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
                } else if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    startRecording();
                }else{
                    Toast.makeText(this, " Permission was denied ", Toast.LENGTH_SHORT).show();
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
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            startRecording();

        }
    }


    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(fileName);
        updateAmplitude(200);
        autoStopRecording(10000);
        updateProgress(1000, 10);
        start.setVisibility(View.GONE);
        try {
            mediaRecorder.prepare();

        } catch (IOException e) {
            Log.e("AudioPrepare", "Prepare failed");
        }

        mediaRecorder.start();

    }


    private void updateAmplitude(final long delayMillis) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    start.setVisibility(View.GONE);
                    explain.setVisibility(View.VISIBLE);
                    explain.setText(getResources().getString(R.string.recorder));
                    recorder.setVisibility(View.VISIBLE);
                    recorder.setText("Input Amplitude - " +  mediaRecorder.getMaxAmplitude());
                    if (mediaRecorder.getMaxAmplitude() < 1000) {
                        insertEarpiece.setVisibility(View.VISIBLE);
                        insertEarpiece.setText(getResources().getString(R.string.warn_increase_voice));
                        insertEarpiece.setTextColor(getResources().getColor(R.color.colorPrimary));
                        recorder.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        insertEarpiece.setVisibility(View.GONE);
                        recorder.setTextColor(getResources().getColor(R.color.green));

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


    private void playBack() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        playBack();
        handler.removeCallbacks(timerTask);
        insertEarpiece.setVisibility(View.GONE);
        recorder.setVisibility(View.GONE);
        explain.setText("Listen while the recorded sound playback...\n Can you hear what you recorded?");
        progressBar.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
        passFail.setVisibility(View.VISIBLE);
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passAction();
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
        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                failAction();
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

    private void failAction() {
        result.setVisibility(View.VISIBLE);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        setDefaults(MIC, FAILED, TestMicrophone.this);
        stopPlayback();
    }

    private void passAction() {
        result.setVisibility(View.VISIBLE);
        result.setText("PASS");
        result.setTextColor(getResources().getColor(R.color.green));
        setDefaults(MIC, SUCCESS, TestMicrophone.this);
        stopPlayback();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopPlayback();
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
