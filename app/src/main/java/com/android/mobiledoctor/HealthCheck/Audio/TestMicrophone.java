package com.android.mobiledoctor.HealthCheck.Audio;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.R;

import java.io.IOException;

import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.MIC;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;

public class TestMicrophone extends AppCompatActivity {

    TextView result, skip, insertEarpiece, explain;
    Button start;
    LinearLayout linearLayout;
    MediaPlayer mp;
    HeadsetStateReceiver receiver;
    Handler handler;
    MediaRecorder mediaRecorder;
    Runnable timerTask;
    private static String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_microphone);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        skip = findViewById(R.id.skip);
        linearLayout = findViewById(R.id.linear);
        explain = findViewById(R.id.explain);
        insertEarpiece = findViewById(R.id.insertEarpiece);
        start = findViewById(R.id.button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(MIC, UNCHECKED, TestMicrophone.this);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(MIC, UNCHECKED, this);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp = new MediaPlayer();
        mediaRecorder = new MediaRecorder();
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        receiver = new HeadsetStateReceiver();
        registerReceiver(receiver, receiverFilter);
    }

    public class HeadsetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if ((action.equals(Intent.ACTION_HEADSET_PLUG))) {
                int headSetState = intent.getIntExtra("state", 0);
                if (headSetState == 0) {
                    start.setVisibility(View.VISIBLE);
                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermission();
                        }
                    });
                    explain.setVisibility(View.VISIBLE);
                    explain.setText(getResources().getString(R.string.now_playing));
                    insertEarpiece.setVisibility(View.GONE);
                } else {
                    warnRemoveHeadphone();

                }
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
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
            startRecording();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording();

                } else {
                    Toast.makeText(this, "Permission was denied ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void startRecording() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i1) {
                    Log.e("Media Error", i + "");
                }
            });
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        start.setVisibility(View.GONE);
        timer(5000);

    }

    private void timer(long delayMillis) {
        timerTask = new Runnable() {
            @Override
            public void run() {

                try {
                    stopRecording();
                    playRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void playRecording() throws IOException {
        mp.setDataSource(fileName);
        mp.prepareAsync();
        mp.setLooping(true);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mp.start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
        unregisterReceiver(receiver);
        //handler.removeCallbacks(timerTask);
    }



    public void passAction(View view) {
        setDefaults(MIC, SUCCESS, TestMicrophone.this);
        finish();
    }

    public void failAction(View view) {
        setDefaults(MIC, FAILED, TestMicrophone.this);
        finish();
    }

}
