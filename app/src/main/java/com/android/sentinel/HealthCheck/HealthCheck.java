package com.android.sentinel.HealthCheck;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.android.sentinel.HealthCheck.Battery.TestBattery;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.HOME;

public class HealthCheck extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    TestFragment testFragment;
    DeviceInfoFragment deviceInfoFragment;
    ViewPager vpPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_check);
        vpPager = findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(0);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(vpPager);
        testFragment = new TestFragment();
        deviceInfoFragment = new DeviceInfoFragment();
        if (getIntent().getStringExtra(FROM) != null && getIntent().getStringExtra(FROM).equals(HOME))
        showDialog();
    }

    private void showDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        //customDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        customDialog.show();

    }

    public class CustomDialog extends Dialog implements android.view.View.OnClickListener {
        Activity activity;
        LinearLayout spotCheck, autoCheck;

        public CustomDialog(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_fragment);
            spotCheck = findViewById(R.id.spotcheck);
            autoCheck = findViewById(R.id.autocheck);
            spotCheck.setOnClickListener(this);
            autoCheck.setOnClickListener(this);
            setCancelable(false);
            setCanceledOnTouchOutside(false);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.spotcheck:
                    vpPager.setCurrentItem(1);
                    break;
                case R.id.autocheck:
                    Intent intent = new Intent(HealthCheck.this, TestBattery.class);
                    intent.putExtra(FROM, "Home");
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DeviceInfoFragment();
                case 1:

                    return new TestFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Device Info";
                case 1:
                    return "Testing";
                    default:
                       return  "Testing";
            }
        }

    }

}
