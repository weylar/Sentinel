package com.android.sentinel.HealthCheck.Audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FLASH;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.SPEAKER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestSpeaker extends AppCompatActivity {
    private static final String TAG = "tag";
    TextView insertEarpiece, explain;
    Button start, skip;
    LinearLayout pass_fail, skip_start;
    MediaPlayer mp;
    HeadsetStateReceiver receiver;
    IntentFilter receiverFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_speaker);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        skip = findViewById(R.id.skip);
        pass_fail = findViewById(R.id.linear);
        skip_start = findViewById(R.id.start_skip);
        explain = findViewById(R.id.explain);
        insertEarpiece = findViewById(R.id.insertEarpiece);
        start = findViewById(R.id.button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(SPEAKER, UNCHECKED, TestSpeaker.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(FLASH)) {
                        Intent intent = new Intent(TestSpeaker.this, TestReceiver.class);
                        intent.putExtra(FROM, SPEAKER);
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
                setDefaults(SPEAKER, UNCHECKED, this);
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
        setDefaults(SPEAKER, UNCHECKED, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp = MediaPlayer.create(TestSpeaker.this, R.raw.audio_playback);
        mp.setLooping(true);
        receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        receiver = new HeadsetStateReceiver();
        registerReceiver(receiver, receiverFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
        mp.release();
        unregisterReceiver(receiver);
    }

    private void warnRemoveHeadphone() {
        pass_fail.setVisibility(View.GONE);
        skip_start.setVisibility(View.VISIBLE);
        if (mp.isPlaying()) {
            mp.pause();
        }
        insertEarpiece.setVisibility(View.VISIBLE);
        insertEarpiece.setText(getResources().getString(R.string.remove_headphone));
        start.setVisibility(View.GONE);
        skip.setVisibility(View.VISIBLE);
        explain.setVisibility(View.GONE);
    }

    private void startPlayback() {
        /*Increase device volume*/
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mp.start();
        explain.setText(getResources().getString(R.string.now_playing));
        pass_fail.setVisibility(View.VISIBLE);
        skip_start.setVisibility(View.GONE);
        start.setVisibility(View.GONE);
        explain.setVisibility(View.VISIBLE);
        insertEarpiece.setVisibility(View.GONE);
    }

    public void passAction(View view) {
        setDefaults(SPEAKER, SUCCESS, TestSpeaker.this);
        if (getIntent().getExtras() != null) {
            String val = getIntent().getStringExtra(FROM);
            if (val.equals(FLASH)) {
                Intent intent = new Intent(TestSpeaker.this, TestReceiver.class);
                intent.putExtra(FROM, SPEAKER);
                startActivity(intent);
            }
        } else {
            finish();
        }
    }

    public void failAction(View view) {
        setDefaults(SPEAKER, FAILED, TestSpeaker.this);
        if (getIntent().getExtras() != null) {
            String val = getIntent().getStringExtra(FROM);
            if (val.equals(FLASH)) {
                Intent intent = new Intent(TestSpeaker.this, TestReceiver.class);
                intent.putExtra(FROM, SPEAKER);
                startActivity(intent);
            }
        } else {
            finish();
        }
    }

    public class HeadsetStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if ((action.equals(Intent.ACTION_HEADSET_PLUG))) {
                int headSetState = intent.getIntExtra("state", 0);
                if (headSetState == 0) {
                    start.setVisibility(View.VISIBLE);
                    explain.setVisibility(View.VISIBLE);
                    insertEarpiece.setVisibility(View.GONE);
                    explain.setText(getResources().getString(R.string.earphone_details_dialog));
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
