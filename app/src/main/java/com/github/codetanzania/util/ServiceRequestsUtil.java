package com.github.codetanzania.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.github.codetanzania.model.ServiceRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tz.co.codetanzania.R;

public class ServiceRequestsUtil {

    public static final String TAG = "ServiceRequestsUtil";

    public static final int TODAY_GROUP      = 0;
    public static final int YESTERDAY_GROUP  = 1;
    public static final int THIS_WEEK_GROUP  = 2;
    public static final int THIS_MONTH_GROUP = 3;
    public static final int OLDEST_GROUP     = 4;


    public static SparseArray<ServiceRequest> fromJson(String json) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                // See http://stackoverflow.com/questions/32431279/android-m-retrofit-json-cant-make-field-constructor-accessible
                // .excludeFieldsWithModifiers(Modifier.STATIC)
                // .excludeFieldsWithoutExposeAnnotation()
                .create();
        JsonElement jsElement = new JsonParser().parse(json);
        Log.d(TAG, "An Object is" + gson.toJson(jsElement));
        JsonObject  jsObject  = jsElement.getAsJsonObject();
        JsonArray   jsArray   = jsObject.getAsJsonArray("servicerequests");
        Log.d(TAG, gson.toJson(jsArray));
        ServiceRequest[] requests = gson.fromJson(jsArray, ServiceRequest[].class);
        SparseArray<ServiceRequest> sparseArray = new SparseArray<>(requests.length);
        for (int i = 0; i < requests.length; i++) {
            sparseArray.append(i, requests[i]);
        }
        return sparseArray;
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

    public static Map<Integer, List<ServiceRequest>> group(List<ServiceRequest> serviceRequestList) {

        Map<Integer, List<ServiceRequest>> retVal =
                new HashMap<>();

        retVal.put(TODAY_GROUP,         new ArrayList<ServiceRequest>());
        retVal.put(YESTERDAY_GROUP,     new ArrayList<ServiceRequest>());
        retVal.put(THIS_WEEK_GROUP,     new ArrayList<ServiceRequest>());
        retVal.put(THIS_MONTH_GROUP,    new ArrayList<ServiceRequest>());
        retVal.put(OLDEST_GROUP,        new ArrayList<ServiceRequest>());

        Date _now     = new Date(); // as we speak
        int  _interval;

        for (ServiceRequest req: serviceRequestList) {
            // if issue has not been resolved yet, use status
            if (req.resolvedAt == null) {
                if (req.status == null) {
                    continue;
                } else {
                    if (req.status.updatedAt == null) {
                        _interval = daysBetween(_now, req.status.createdAt);
                    } else {
                        _interval = daysBetween(_now, req.status.updatedAt);
                    }
                }
            } else {
                _interval = daysBetween(_now, req.resolvedAt);
            }

            if (_interval <= 1) {
                retVal.get(TODAY_GROUP).add(req);
            } else if (_interval > 1 && _interval <= 2) {
                retVal.get(YESTERDAY_GROUP).add(req);
            } else if (_interval > 2 && _interval <= 7) {
                retVal.get(THIS_WEEK_GROUP).add(req);
            } else {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(_now);
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(req.resolvedAt != null ? req.resolvedAt :
                        (req.status.updatedAt != null ? req.status.updatedAt : req.status.createdAt));
                if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                    retVal.get(THIS_MONTH_GROUP).add(req);
                } else {
                    retVal.get(OLDEST_GROUP).add(req);
                }
            }

            Log.d(TAG, String.valueOf(_interval));
        }

        return retVal;
    }

    public static String getI18NTitle(Context mContext, int index) {
        return mContext.getResources().getStringArray(R.array.duration_names)[index];
    }
}
