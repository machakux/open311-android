package com.github.codetanzania.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.codetanzania.model.Jurisdiction;
import com.github.codetanzania.model.Reporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static final String TAG = "Util";

    public enum RunningMode {
        FIRST_TIME_INSTALL,
        FIRST_TIME_UPGRADE
    }


    public static boolean isFirstRun(Context mContext, RunningMode mRunningMode) throws Exception {

        int currentVersionCode, savedVersionCode;

        try {
            currentVersionCode = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0)
                    .versionCode;

        } catch(android.content.pm.PackageManager.NameNotFoundException e) {
            Log.e(TAG, String.format("An exception is %s", e.getMessage()));
            throw new Exception(
                    String.format("Package name not found. Original exception was: %s ", e.getMessage()));
        }

        SharedPreferences sharedPrefs = mContext
                .getSharedPreferences(AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);

        savedVersionCode = sharedPrefs.getInt(AppConfig.Const.APP_VERSION_CODE, -1);

        boolean firstTimeRun = savedVersionCode == -1;
        boolean upgradeRun   = savedVersionCode <  currentVersionCode;

        if (firstTimeRun || upgradeRun) {
            sharedPrefs.edit().putInt(
                    AppConfig.Const.APP_VERSION_CODE, currentVersionCode).apply();
        }

        if (mRunningMode == RunningMode.FIRST_TIME_INSTALL) {
            return firstTimeRun;
        } else {
            return mRunningMode == RunningMode.FIRST_TIME_UPGRADE && upgradeRun;
        }
    }

    public static Reporter getCurrentReporter(Context mContext) {
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(
                AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
        String phone = sharedPrefs.getString(AppConfig.Const.REPORTER_PHONE, null);

        // logical to use phone number which we verify through OTP
        if (phone == null) {
            return null;
        }

        String email = sharedPrefs.getString(AppConfig.Const.REPORTER_EMAIL, null);
        String account = sharedPrefs.getString(AppConfig.Const.REPORTER_DAWASCO_ACCOUNT, null);
        String fullName = sharedPrefs.getString(AppConfig.Const.REPORTER_NAME, null);

        Reporter reporter = new Reporter();
        reporter.account = account;
        reporter.phone = phone;
        reporter.name = fullName;
        reporter.email = email;

        return reporter;
    }

    public static void storeCurrentReporter(Context mContext, Reporter reporter) {
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(
                AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPrefs.edit()
                .putString(AppConfig.Const.REPORTER_NAME, reporter.name)
                .putString(AppConfig.Const.REPORTER_PHONE, reporter.phone)
                .putString(AppConfig.Const.REPORTER_EMAIL, reporter.email)
                .putString(AppConfig.Const.REPORTER_DAWASCO_ACCOUNT, reporter.account)
                .apply();
    }

    public static void storeAuthToken(Context mContext, String mToken) {
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
            sharedPrefs.edit()
                    .putString(AppConfig.Const.AUTH_TOKEN, mToken)
                    .apply();
    }

    public static String getCurrentUserId(Context mContext) {
        SharedPreferences mPrefs = mContext.getSharedPreferences(AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
        return mPrefs.getString(AppConfig.Const.CURRENT_USER_ID, null);
    }

    public static String getAuthToken(Context mContext) {
        SharedPreferences mPrefs = mContext.getSharedPreferences(AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
        return mPrefs.getString(AppConfig.Const.AUTH_TOKEN, null);
    }

    public static void storeUserId(Context mContext, String mUserId) {
        SharedPreferences mPrefs = mContext.getSharedPreferences(AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
        mPrefs.edit()
                .putString(AppConfig.Const.CURRENT_USER_ID, mUserId)
                .apply();
    }

    public static String parseJWTToken(String input) throws JSONException {
        JSONObject jsObj = new JSONObject(input);
        return jsObj.getString("token");
    }

    public static String parseUserId(String input) throws JSONException {
        JSONObject jsObj = new JSONObject(input);
        return jsObj.getJSONObject("party").getString("_id");
    }

    public static String inferContentType(@NonNull String urlStr) {

        String exts[][] =
            {{"aac", "mp3", "ogg"},
             {"flv", "mp4", "webm"},
             {"jpeg", "jpg", "png"}};

        String parties[] = {"audio", "video", "image"};
        int index; String ext = "binary/octet-stream", haystack = urlStr.substring(1 + urlStr.lastIndexOf("."));
        Log.d(TAG, haystack);
        for (int i = 0; i < exts.length; ++i) {
            index = Arrays.binarySearch(exts[i], haystack);
            if (index >= 0) {
                ext = String.format("%s/%s", parties[i], exts[i][index]);
                break;
            }
        }
        return ext;
    }

    public static String formatDate(
            @NonNull Date d, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
        return sdf.format(d);
    }

    public static Jurisdiction getReporterJurisdiction(Context mContext) {
        throw new UnsupportedOperationException("method not implemented yet");
    }

    public static void storeReporterJurisdiction(Context mContext, Jurisdiction jurisdiction) {
        throw new UnsupportedOperationException("method not implemented yet");
    }

    public static void resetPreferences(Context mContext) {
        mContext.getSharedPreferences(
                AppConfig.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
