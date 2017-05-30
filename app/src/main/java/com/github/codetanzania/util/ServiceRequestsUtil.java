package com.github.codetanzania.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

import com.github.codetanzania.Constants;
import com.github.codetanzania.model.ServiceRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tz.co.codetanzania.R;

public class ServiceRequestsUtil {

    public static final String TAG = "ServiceRequestsUtil";

    public static final void save(Context ctx, ServiceRequest[] requests) {
        // save the requests to the shared preferences
        SharedPreferences mPrefs = ctx.getSharedPreferences(
                Constants.Const.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public static ArrayList<ServiceRequest> fromJson(String json) throws IOException {

        Log.d(TAG, "Trying to convert " + json + "Inot something useful a system can understand.");

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                // See http://stackoverflow.com/questions/32431279/android-m-retrofit-json-cant-make-field-constructor-accessible
                // .excludeFieldsWithModifiers(Modifier.STATIC)
                // .excludeFieldsWithoutExposeAnnotation()
                .create();
        JsonElement jsElement = new JsonParser().parse(json);
        Log.d(TAG, "An Object is " + gson.toJson(jsElement));
        JsonObject  jsObject  = jsElement.getAsJsonObject();
        JsonArray   jsArray   = jsObject.getAsJsonArray("servicerequests");
        Log.d(TAG, gson.toJson(jsArray));
        ServiceRequest[] requests = gson.fromJson(jsArray, ServiceRequest[].class);
        ArrayList<ServiceRequest> list = new ArrayList<>(requests.length);
        for (int i = 0; i < requests.length; i++) {
            list.add(requests[i]);
        }
        return list;
    }

    public static int daysBetween(Date date1, Date date2){
        Calendar dayOne = Calendar.getInstance(),
                dayTwo = Calendar.getInstance();
        dayOne.setTime(date1);
        dayTwo.setTime(date2);

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }
}
