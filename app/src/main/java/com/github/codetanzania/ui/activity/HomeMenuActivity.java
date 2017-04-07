package com.github.codetanzania.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.codetanzania.ui.HomeMenu;

import tz.co.codetanzania.R;

public class HomeMenuActivity extends AppCompatActivity implements HomeMenu.OnClickListener {

    private static final String TAG = "HomeMenuActivity";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
    }

    @Override
    public void onClickPerformed(HomeMenu.ClickEvent evt) {
        Log.d(TAG, String.format("Evt: %s", evt));
    }
}