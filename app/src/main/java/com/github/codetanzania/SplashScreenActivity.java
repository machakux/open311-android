package com.github.codetanzania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.codetanzania.api.Open311Api;
import com.github.codetanzania.model.Reporter;
import com.github.codetanzania.util.Util;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity implements Callback<ResponseBody> {

    public static final String TAG = "SplashScreen";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getJWTToken();
    }

    private void getJWTToken() {
        Reporter reporter = Util.getCurrentReporter(this);
        if (reporter == null || TextUtils.isEmpty(reporter.email) && TextUtils.isEmpty(reporter.phone)) {
            startActivity(new Intent(this, IDActivity.class));
            finish();
        } else {

            // todo: remove the next hardcoded lines when the api is ready to work with phone numbers
            Map<String, String> map = new HashMap();
            map.put("email", "lallyelias87@gmail.com");
            map.put("password", "open311@qwerty");
            new Open311Api
                .ServiceBuilder(this)
                .build(Open311Api.AuthEndpoint.class)
                .signIn(map)
                .enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        Log.d(TAG, "Received respose from server");
        if (response.isSuccessful()) {
            Log.d(TAG, "response was ok");
            try {
                String jsonString = response.body().string();
                String token = Util.parseJWTToken(jsonString);
                Util.storeAuthToken(this, token);
                // String userId = Util.parseUserId(jsonString);
                // Log.d(TAG, "user identity is " + userId);
                // Util.storeUserId(this, userId);
                Log.d(TAG, "response body was " + token);
                // go to issues
                startActivity(new Intent(this, IssueTicketGroupsActivity.class));
                // we won't come back here again
                finish();
            } catch (IOException ioException) {
                // show an error
                Toast.makeText(this, "Error authenticating user.", Toast.LENGTH_LONG)
                        .show();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            if (response.code() == 500) {
                Toast.makeText(this, "Internal server error. Please try again later.", Toast.LENGTH_LONG)
                    .show();
            }
            Log.w(TAG, "Response was not ok");
            Log.w(TAG, "HTTP Response was " + response.code() + " " + response.message());
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
        Log.e(TAG, "An error was " + t.getMessage());
        startActivity(new Intent(this, ErrorActivity.memoizeClass(SplashScreenActivity.class)));
        finish();
    }
}
