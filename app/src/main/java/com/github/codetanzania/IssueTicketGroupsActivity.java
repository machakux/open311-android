package com.github.codetanzania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.github.codetanzania.api.Open311Api;
import com.github.codetanzania.fragment.ErrorFragment;
import com.github.codetanzania.fragment.ProgressBarFragment;
import com.github.codetanzania.fragment.ServiceRequestsFragment;
import com.github.codetanzania.model.ServiceRequest;
import com.github.codetanzania.model.adapter.ServiceRequests;
import com.github.codetanzania.util.ServiceRequestsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.codetanzania.R;

/* tickets activity. load and display tickets from the server */
public class IssueTicketGroupsActivity extends AppCompatActivity
    implements ErrorFragment.OnReloadClickListener, Callback<ResponseBody> {

    /* used by the logcat */
    private static final String TAG = "TicketGroupsActivity";

    /* ProgressBarFragment */
    private ProgressBarFragment mProgressBarFrag;

    /* ErrorFragment */
    private ErrorFragment mErrorFrag;

    /* ServiceRequestsFragment */
    private ServiceRequestsFragment mServiceRequestsFrag;

    /* Floating Action bar button */
    private FloatingActionButton mFab;

    /* An error flag */
    private boolean isErrorState = false;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_tickets_group);
    }

    @Override public void onResume() {
        super.onResume();
        loadServiceRequests();
    }

    @Override public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mFab = (FloatingActionButton) findViewById(R.id.fab_ReportIssue);
    }

    private void loadServiceRequests() {
        mProgressBarFrag = ProgressBarFragment.getInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_TicketsActivity, mProgressBarFrag)
                .disallowAddToBackStack()
                .commit();

        // load data from the server
        try {
            Open311Api.ServiceBuilder api = new Open311Api.ServiceBuilder(this);
            api.build(Open311Api.ServiceRequestEndpoint.class)
                .getByUserId().enqueue(this);

        } catch (Exception e) {
            Log.e(TAG, "An error was " + e.getMessage());
        }
    }

    private Bundle packForError(String msg, int icn) {
        Bundle args = new Bundle();
        args.putString(ErrorFragment.ERROR_MSG, msg);
        args.putInt(ErrorFragment.ERROR_ICN, icn);
        return args;
    }

    private void displayServiceRequests(SparseArray<ServiceRequest> requests) {
        Bundle args = new Bundle();
        args.putSparseParcelableArray(
                ServiceRequestsFragment.SERVICE_REQUESTS, requests);
        mServiceRequestsFrag = ServiceRequestsFragment.getInstance(args);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.ll_TicketsActivity, mServiceRequestsFrag)
            .disallowAddToBackStack()
            .commitAllowingStateLoss();

        // enable the fab
        mFab.animate().alpha(1.0f);
    }

    private void displayError() {

        if (isErrorState) {
            return;
        }

        Bundle args = packForError(
            getString(R.string.msg_server_error), R.drawable.ic_cloud_off_48x48);

        mErrorFrag = ErrorFragment.getInstance(args);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.ll_TicketsActivity, mErrorFrag)
            .disallowAddToBackStack()
            .commit();

        // disable the fab
        mFab.animate().alpha(0.0f);
    }

    @Override
    public void onResponse(
        Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            // show data
            ResponseBody data = response.body();
            if (data != null) {
                try {
                    String body = data.string();
                    Log.d(TAG, "DATA: " + body);
                    isErrorState = false;
                    displayServiceRequests(
                            ServiceRequestsUtil.fromJson(body)
                    );
                } catch (IOException e) {
                    Log.e(TAG, String.format("An error was %s", e.getMessage()));
                }
                // displayServiceRequests(data.requests);
            } else {
                // show empty msg
                isErrorState = false;
            }
        } else {
            isErrorState = true;
            // show error
            displayError();
            try {
                Log.e(TAG, response.code() + ". " + response.message());
                Log.e(TAG, response.errorBody().string());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            Toast.makeText(this, R.string.msg_server_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(
        Call<ResponseBody> call, Throwable t) {
       displayError();

        // debug
        if ( t != null ) {
            Log.e(TAG, "ERROR: " + t.getMessage());
        }

        // flash error message to the user
        Toast.makeText(this, R.string.msg_server_error, Toast.LENGTH_LONG).show();
        isErrorState = true;
    }

    @Override
    public void onReloadClicked() {

        // reload the activity
        startActivity(new Intent(this, IssueTicketGroupsActivity.class));
        finish();
    }
}
