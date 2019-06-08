package com.android.sentinel.HealthCheck.Audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import java.io.IOException;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FLASH;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.RECEIVER;
import static com.android.sentinel.HealthCheck.TestFragment.SPEAKER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestReceiver extends AppCompatActivity {
    TextView skip, insertEarpiece, explain;
    Button start;
    LinearLayout pass_fail, skip_start;
    MediaPlayer mp;
    HeadsetStateReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_receiver);
        pass_fail = findViewById(R.id.linear);
        skip_start = findViewById(R.id.start_skip);
        skip = findViewById(R.id.skip);
        explain = findViewById(R.id.explain);
        insertEarpiece = findViewById(R.id.insertEarpiece);
        start = findViewById(R.id.button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(RECEIVER, UNCHECKED, TestReceiver.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(SPEAKER)) {
                        Intent intent = new Intent(TestReceiver.this, TestMicrophone.class);
                        intent.putExtra(FROM, RECEIVER);
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
                setDefaults(RECEIVER, UNCHECKED, this);
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
        setDefaults(RECEIVER, UNCHECKED, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp = new MediaPlayer();
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        receiver = new HeadsetStateReceiver();
        registerReceiver(receiver, receiverFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            mp.stop();
        }
        unregisterReceiver(receiver);
    }

    public void passAction(View view) {
        setDefaults(RECEIVER, SUCCESS, TestReceiver.this);
        if (getIntent().getExtras() != null) {
            String val = getIntent().getStringExtra(FROM);
            if (val.equals(SPEAKER)) {
                Intent intent = new Intent(TestReceiver.this, TestMicrophone.class);
                intent.putExtra(FROM, RECEIVER);
                startActivity(intent);
            }
        }else {
            finish();
        }
    }

    public void failAction(View view) {
        setDefaults(RECEIVER, FAILED, TestReceiver.this);
        if (getIntent().getExtras() != null) {
            String val = getIntent().getStringExtra(FROM);
            if (val.equals(SPEAKER)) {
                Intent intent = new Intent(TestReceiver.this, TestMicrophone.class);
                intent.putExtra(FROM, RECEIVER);
                startActivity(intent);
            }
        }else {
            finish();
        }
    }

    private void warnRemoveHeadphone() {
        pass_fail.setVisibility(View.GONE);
        skip_start.setVisibility(View.VISIBLE);
        if (mp.isPlaying()) {
            mp.pause();
            mp.release();
            mp = null;

        }
        insertEarpiece.setVisibility(View.VISIBLE);
        insertEarpiece.setText(getResources().getString(R.string.remove_headphone));
        start.setVisibility(View.GONE);
        skip.setVisibility(View.VISIBLE);
        explain.setVisibility(View.GONE);
    }

    private void startPlayback() {
        mp = new MediaPlayer();
        AudioManager audioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        audioManager.setSpeakerphoneOn(false);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.audio_playback);
        if (afd == null) return;
        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mp.setLooping(true);
        try {
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepareAsync();
            afd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mp.start();
            }
        });
        explain.setText(getResources().getString(R.string.now_playing_receiver));
        pass_fail.setVisibility(View.VISIBLE);
        start.setVisibility(View.GONE);
        skip_start.setVisibility(View.GONE);
        explain.setVisibility(View.VISIBLE);


    }


    public class HeadsetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if ((action.equals(Intent.ACTION_HEADSET_PLUG))) {
                int headSetState = intent.getIntExtra("state", 0);
                if (headSetState == 0) {
                    insertEarpiece.setVisibility(View.GONE);
                    explain.setVisibility(View.VISIBLE);
                    explain.setText(getResources().getString(R.string.earphone_details_dialog));
                    start.setVisibility(View.VISIBLE);
                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startPlayback();
                        }
                    });
                } else {
                    warnRemoveHeadphone();

                }
            }
        }
    }
}
