package com.github.codetanzania;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.codetanzania.model.Reporter;
import com.github.codetanzania.util.Util;

import tz.co.codetanzania.R;

public class CivilianProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_EditProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                    CivilianProfileActivity.this, EditProfileActivity.class));
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Reporter reporter = Util.getCurrentReporter(this);
        assert reporter != null;

        if (!TextUtils.isEmpty(reporter.name)) {
            actionBar.setTitle(reporter.name);
        }

        TextView tvPhoneNumber = (TextView) findViewById(R.id.tv_UserPhone);
        if (TextUtils.isEmpty(reporter.phone)) {
            tvPhoneNumber.setText(R.string.text_empty_phone);
        } else {
            tvPhoneNumber.setText(reporter.phone);
        }

        TextView tvEmail = (TextView) findViewById(R.id.tv_UserEmail);
        if (TextUtils.isEmpty(reporter.email)) {
            tvEmail.setText(R.string.text_empty_email);
        } else {
            tvEmail.setText(reporter.email);
        }

        TextView tvLocation = (TextView) findViewById(R.id.tv_UserLocation);
        tvLocation.setText(R.string.text_empty_location);

        TextView tvMeterNumber = (TextView) findViewById(R.id.tv_UserMeterNumber);
        tvMeterNumber.setText(R.string.text_empty_meter_number);

        View btnLogout = findViewById(R.id.btn_Logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmExit();
            }
        });
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.text_confirm_logout)
                .setPositiveButton(R.string.action_logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Util.resetPreferences(CivilianProfileActivity.this);
                        startActivity(new Intent(CivilianProfileActivity.this, IDActivity.class));
                        finish();
                    }
                })
                .setNegativeButton(R.string.action_stay, null)
                .show();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
