package com.github.codetanzania;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.github.codetanzania.model.Reporter;
import com.github.codetanzania.util.Util;

import tz.co.codetanzania.R;

public class EditProfileActivity extends AppCompatActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set content view
        setContentView(R.layout.activity_edit_profile);
        // retrieve current reporter
        bindDataToViews();
    }

    private void bindDataToViews() {
        Reporter reporter = Util.getCurrentReporter(this);
        // todo: uncomment next line when Reporter#meterNumber is implemented
        // EditText etMeterNumber = (EditText) findViewById(R.id.et_MeterNumber);
        EditText etAccountNumber = (EditText) findViewById(R.id.et_AccountNumber);
        EditText etZipCode = (EditText) findViewById(R.id.et_ZipCode);
        EditText etPhoneNumber = (EditText) findViewById(R.id.et_phoneNumber);
        EditText etUserEmail = (EditText) findViewById(R.id.et_UserEmail);
        EditText etUserName = (EditText) findViewById(R.id.et_userName);

        assert reporter != null;
        // now ...
        if (!TextUtils.isEmpty(reporter.account)) {
            etAccountNumber.setText(reporter.account);
        }

        if (!TextUtils.isEmpty(reporter.phone)) {
            if (reporter.phone.startsWith("255")) {
                etZipCode.setText(reporter.phone.substring(0, 3));
                etPhoneNumber.setText(reporter.phone.substring(3));
            } else {
                etPhoneNumber.setText(reporter.phone);
            }
        }

        if (!TextUtils.isEmpty(reporter.name)) {
            etUserName.setText(reporter.name);
        }

        if (!TextUtils.isEmpty(reporter.email)) {
            etUserEmail.setText(reporter.email);
        }
    }

    // bind event
    public void bindEventsToViews () {
        View fab = findViewById(R.id.fab_EditProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(view, "Updating your profile...", Snackbar.LENGTH_LONG)
                        .setAction("Hide", null).show();
                // todo: post data to the server
            }
        });
    }
}
