package com.github.codetanzania.api;

import android.content.Context;

import com.github.codetanzania.model.Reporter;
import com.github.codetanzania.model.ServiceGroup;
import com.github.codetanzania.model.adapter.ServiceRequests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import tz.co.codetanzania.R;

public class Open311Api {

    public interface ServiceGroupEndpoint {
        @GET("/servicegroups")
        @Headers({
                "Content-Type: application/json; charset='utf-8'",
                "Accept: application/json"
        })
        Call<List<ServiceGroup>> get(@Header("Authorization") String jwtToken);
    }

    public interface ServiceRequestEndpoint {
        @GET("/servicerequests")
        @Headers({"Accept: application/json"})
        Call<ServiceRequests> getByUserId(/*@Header("Authorization")*/String jwtToken);

        @GET("/servicerequests")
        @Headers({"Accept: application/json"})
        Call<ResponseBody> getByUserId();
    }

    public interface AuthEndpoint {
        @POST("/signin")
        @Headers({"Accept: application/json", "Content-Type: application/json"})
        Call<ResponseBody> signIn(@Body Reporter reporter);
    }

    public static class ServiceBuilder {

        private Context mContext;

        public ServiceBuilder(Context mContext) {
            this.mContext = mContext;
        }

        private Retrofit retrofit() {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            return new Retrofit.Builder()
                    .baseUrl(mContext.getString(R.string.enpoint_url))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        public ServiceRequestEndpoint getServiceRequests() {
            return retrofit().create(ServiceRequestEndpoint.class);
        }

        public <T> T build(Class<T> clazz) {
            return retrofit().create(clazz);
        }
    }
}
