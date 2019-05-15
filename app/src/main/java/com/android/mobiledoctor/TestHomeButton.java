package com.android.mobiledoctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static com.android.mobiledoctor.MainActivity.FAILED;
import static com.android.mobiledoctor.MainActivity.FLASH;
import static com.android.mobiledoctor.MainActivity.HOME;
import static com.android.mobiledoctor.MainActivity.SUCCESS;
import static com.android.mobiledoctor.MainActivity.setDefaults;

public class TestHomeButton extends AppCompatActivity {
    HomeWatcher mHomeWatcher;
    Runnable timerTask;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_home_button);




    }


    private void timer(long delayMillis) {
       timerTask = new Runnable() {
            @Override
            public void run() {
                setDefaults(HOME, FAILED, TestHomeButton.this);
                Toast.makeText(TestHomeButton.this, "Sorry! Test Failed!", Toast.LENGTH_SHORT).show();
                finish();

            }
        };
        handler = new Handler();
        handler.postDelayed(timerTask, delayMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer(10000);
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                setDefaults(HOME, SUCCESS, TestHomeButton.this);
                Toast.makeText(TestHomeButton.this, "Home Pressed, test Passed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHomeWatcher.stopWatch();
        handler.removeCallbacks(timerTask);
    }

    public class HomeWatcher {

        static final String TAG = "hg";
        private Context mContext;
        private IntentFilter mFilter;
        private OnHomePressedListener mListener;
        private InnerReceiver mReceiver;

        public HomeWatcher(Context context) {
            mContext = context;
            mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        }

        public void setOnHomePressedListener(OnHomePressedListener listener) {
            mListener = listener;
            mReceiver = new InnerReceiver();
        }

        public void startWatch() {
            if (mReceiver != null) {
                mContext.registerReceiver(mReceiver, mFilter);
            }
        }

        public void stopWatch() {
            if (mReceiver != null) {
                mContext.unregisterReceiver(mReceiver);
            }
        }

        class InnerReceiver extends BroadcastReceiver {
            final String SYSTEM_DIALOG_REASON_KEY = "reason";
            final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
            final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
            final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null) {
                        Log.e(TAG, "action:" + action + ",reason:" + reason);
                        if (mListener != null) {
                            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                                mListener.onHomePressed();
                            } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                                mListener.onHomeLongPressed();
                            }
                        }
                    }
                }
            }
        }
    }

    public interface OnHomePressedListener {
        void onHomePressed();

        void onHomeLongPressed();
    }
}

