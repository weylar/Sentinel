package com.android.mobiledoctor.HealthCheck.Audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.R;

import static com.android.mobiledoctor.HealthCheck.TestFragment.EARPHONE;
import static com.android.mobiledoctor.HealthCheck.TestFragment.FAILED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.SUCCESS;
import static com.android.mobiledoctor.HealthCheck.TestFragment.UNCHECKED;
import static com.android.mobiledoctor.HealthCheck.TestFragment.setDefaults;

public class TestEarphone extends AppCompatActivity {
    TextView skip, insertEarpiece, explain;
    Button start;
    LinearLayout linearLayout, startSkip;
    MediaPlayer mp;
    HeadsetStateReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_earphone);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        skip = findViewById(R.id.skip);
        startSkip = findViewById(R.id.start_skip);
        linearLayout = findViewById(R.id.linear);
        explain = findViewById(R.id.explain);
        insertEarpiece = findViewById(R.id.insertEarpiece);
        start = findViewById(R.id.button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlay();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(EARPHONE, UNCHECKED, TestEarphone.this);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(EARPHONE, UNCHECKED, this);
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
        setDefaults(EARPHONE, UNCHECKED, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp = MediaPlayer.create(TestEarphone.this, R.raw.audio_playback);
        mp.setLooping(true);
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
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

    public class HeadsetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if ((action.equals(Intent.ACTION_HEADSET_PLUG))) {
                int headSetState = intent.getIntExtra("state", 0);
                if (headSetState == 0) {
                    warnNoHeadphone();
                } else {
                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startPlay();
                        }
                    });

                    insertEarpiece.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    startSkip.setVisibility(View.VISIBLE);
                    explain.setVisibility(View.VISIBLE);
                    explain.setText(getResources().getString(R.string.earphone_details_dialog));
                    start.setVisibility(View.VISIBLE);



                }
            }
        }
    }

    private void startPlay() {
        mp.start();
        linearLayout.setVisibility(View.VISIBLE);
        startSkip.setVisibility(View.GONE);
        explain.setVisibility(View.VISIBLE);
        explain.setText(getResources().getString(R.string.now_playing));

    }

    private void warnNoHeadphone() {
        if (mp.isPlaying()){
            mp.pause();
        }
        linearLayout.setVisibility(View.GONE);
        startSkip.setVisibility(View.VISIBLE);
        insertEarpiece.setVisibility(View.VISIBLE);
        insertEarpiece.setText(getResources().getString(R.string.insert_headphone));
        start.setVisibility(View.GONE);
        explain.setVisibility(View.GONE);

    }

    public void passAction(View view) {
        setDefaults(EARPHONE, SUCCESS, TestEarphone.this);
        finish();
    }

    public void failAction(View view) {
        setDefaults(EARPHONE, FAILED, TestEarphone.this);
        finish();
    }

}
