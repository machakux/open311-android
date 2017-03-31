package com.github.codetanzania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by anon on 3/17/17.
 */

public class SplashScreenActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onStart() {
        super.onStart();
        // TODO: Do authorization using JWT
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, IssueTicketGroupsActivity.class));
                finish();
            }
        }, 1000);
    }
}
