package com.android.sentinel;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.sentinel.HealthCheck.HealthCheck;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        /*Checks permission before showing dialog*/
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            showDialog();
        }
    }


    public void subscriptionClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(R.layout.sub_dialog_view);
        builder.create().show();

    }

    public void healthCheck(View view) {
        Intent intent = new Intent(Home.this, HealthCheck.class);
        startActivity(intent);

    }

    public void report(View view) {
        Intent intent = new Intent(Home.this, Report.class);
        startActivity(intent);

    }

    public void support(View view) {
        Intent intent = new Intent(Home.this, Support.class);
        startActivity(intent);

    }

    public void TradeIn(View view) {
        Intent intent = new Intent(Home.this, Trade.class);
        startActivity(intent);

    }

    public void DataBackup(View view) {
        Intent intent = new Intent(Home.this, DataBackup.class);
        startActivity(intent);

    }

    public void Claim(View view) {
        Intent intent = new Intent(Home.this, Claim.class);
        startActivity(intent);

    }

    public void editProfile(View view) {
        Intent intent = new Intent(Home.this, Profile.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.help) {
            return true;
        } else if (id == R.id.sign) {
            startActivity(new Intent(this, Registration.class));
            return true;
        } else if (id == R.id.about) {
            startActivity(new Intent(this, About.class));
            return true;
        } else if (id == R.id.settings) {
            startActivity(new Intent(this, Setting.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        CustomDialog customDialog = new CustomDialog(this);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        customDialog.show();
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_check_health) {
            Intent intent = new Intent(Home.this, HealthCheck.class);
            startActivity(intent);
        } else if (id == R.id.nav_claim) {
            Intent intent = new Intent(Home.this, Claim.class);
            startActivity(intent);
        } else if (id == R.id.nav_trade) {
            Intent intent = new Intent(Home.this, Trade.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Home.this, Profile.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(Home.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(Home.this, Contact.class);
            startActivity(intent);
        } else if (id == R.id.nav_faq) {
            Intent intent = new Intent(Home.this, Faq.class);
            startActivity(intent);
        } else if (id == R.id.nav_terms_conditions) {
            Intent intent = new Intent(Home.this, Tac.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
//        TODO: Please note these UI have been created Bolade
        //Registration UI
        //Value UI
        //Login UI
    }

    public class CustomDialog extends Dialog
            implements android.view.View.OnClickListener {

        Activity activity;
        Button yes, no;
        TextView heading, details;

        public CustomDialog(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog);
            Button yes = findViewById(R.id.yes);
            TextView heading = findViewById(R.id.heading);
            TextView details = findViewById(R.id.details);
            heading.setText(getResources().getString(R.string.permission_heading));
            details.setText(getResources().getString(R.string.permission_details));
            Button no = findViewById(R.id.no);
            no.setVisibility(View.GONE);
            yes.setOnClickListener(this);
            yes.setText("GO TO SETTINGS");


        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.yes:
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}
