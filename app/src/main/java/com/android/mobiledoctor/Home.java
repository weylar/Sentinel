package com.android.mobiledoctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.android.mobiledoctor.HealthCheck.HealthCheck;
import com.android.mobiledoctor.HealthCheck.TestFragment;

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
        } else if (id == R.id.about) {
            return true;
        } else if (id == R.id.settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_check_health) {
            Intent intent = new Intent(Home.this, HealthCheck.class);
            startActivity(intent);
        } else if (id == R.id.nav_claim) {
            Intent intent = new Intent(Home.this, Registration.class);
            startActivity(intent);
        } else if (id == R.id.nav_trade) {
            Intent intent = new Intent(Home.this, Value.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Home.this, Profile.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {

        } else if (id == R.id.nav_faq) {

        } else if (id == R.id.nav_terms_conditions) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
