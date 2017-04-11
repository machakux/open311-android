package com.github.codetanzania.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.codetanzania.adapter.OnItemClickListener;
import com.github.codetanzania.api.Open311Api;
import com.github.codetanzania.ui.fragment.EmptyIssuesFragment;
import com.github.codetanzania.ui.fragment.ErrorFragment;
import com.github.codetanzania.ui.fragment.ProgressBarFragment;
import com.github.codetanzania.ui.fragment.ServiceRequestsFragment;
import com.github.codetanzania.model.Reporter;
import com.github.codetanzania.model.ServiceRequest;
import com.github.codetanzania.util.AppConfig;
import com.github.codetanzania.util.ServiceRequestsUtil;
import com.github.codetanzania.util.Util;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.codetanzania.R;

/* tickets activity. load and display tickets from the server */
public class IssueTicketGroupsActivity extends AppCompatActivity
    implements ErrorFragment.OnReloadClickListener, Callback<ResponseBody>, OnItemClickListener<ServiceRequest> {

    /* used by the logcat */
    private static final String TAG = "TicketGroupsActivity";

    /* Floating Action bar button */
    private FloatingActionButton mFab;

    /* Frame layout */
    private FrameLayout mFrameLayout;

    /* An error flag */
    private boolean isErrorState = false;

    /*
     * Menu items will be hidden when different fragment
     * than ServiceRequestsFragment is committed
     */
    private MenuItem mSearchMenuItem;
    private MenuItem mSwitchCompat;
    private MenuItem mUserProfileMenuItem;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_tickets_group);
        mFrameLayout = (FrameLayout) findViewById(R.id.frl_TicketsActivity);
    }

    @Override public void onResume() {
        super.onResume();

        try {
            // check if the application is installed for the first time or
            // if the reporter has not signed in yet.
            // if it is, then we start the verification activity (through OTP)
            if (Util.isFirstRun(this, Util.RunningMode.FIRST_TIME_INSTALL) || Util.getCurrentReporter(this) == null) {
                Intent verificationIntent = new Intent(this, IDActivity.class);
                startActivity(verificationIntent);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadServiceRequests();
    }

    @Override public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mFab = (FloatingActionButton) findViewById(R.id.fab_ReportIssue);
        mFab.setAlpha(0.0f);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.issues, menu);
//        mSwitchCompat = menu.findItem(R.id.item_toggle_pending);
//        final SwitchCompat switchCompat = (SwitchCompat) mSearchMenuItem.getActionView();
//        mSearchMenuItem = menu.findItem(R.id.item_search);
//        mUserProfileMenuItem = menu.findItem(R.id.item_user_acc);
//        // default -- show resolved issues
//        switchCompat.setChecked(true);
//        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_user_acc:
                startActivity(new Intent(this, CivilianProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* show or hide menu items */
    private void showMenuItems(boolean show) {
//        mSearchMenuItem.setVisible(show);
//        mSwitchCompat.setVisible(show);
        // mUserProfileMenuItem.setVisible(show);
    }

    private void loadServiceRequests() {

        // hide controls. no need to show them while data is being loaded
        showMenuItems(false);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();
        lp.gravity = Gravity.CENTER;

        ProgressBarFragment mProgressBarFrag = ProgressBarFragment.getInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frl_TicketsActivity, mProgressBarFrag)
                .disallowAddToBackStack()
                .commitAllowingStateLoss();

        String token = Util.getAuthToken(this);
        Reporter reporter = Util.getCurrentReporter(this);
        assert reporter != null;
        String queryParams = String.format("{\"reporter.phone\":\"%s\"}", "255765952971");
        Log.e(TAG, queryParams);
        assert token != null;

        // load data from the server
        try {
            Open311Api.ServiceBuilder api = new Open311Api.ServiceBuilder(this);
            api.build(Open311Api.ServiceRequestEndpoint.class)
                .getByUserId(queryParams, String.format("Bearer %s", token)).enqueue(this);

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

        EmptyIssuesFragment frag = EmptyIssuesFragment.getNewInstance(null);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();

        if (requests.size() == 0) {
            // show empty issues message
            lp.gravity = Gravity.CENTER;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frl_TicketsActivity, frag)
                    .disallowAddToBackStack()
                    .commitAllowingStateLoss();
        } else {
            args.putSparseParcelableArray(
                    ServiceRequestsFragment.SERVICE_REQUESTS, requests);
            ServiceRequestsFragment mServiceRequestsFrag = ServiceRequestsFragment.getNewInstance(args);
            lp.gravity = Gravity.TOP;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frl_TicketsActivity, mServiceRequestsFrag)
                    .disallowAddToBackStack()
                    .commitAllowingStateLoss();

            // show menu items only when we have more than
            showMenuItems(true);
        }

        // show the fab
        mFab.animate().alpha(1.0f);
    }

    private void displayError() {

        if (isErrorState) {
            return;
        }

        // hide controls. no need to show them here
        showMenuItems(false);

        Bundle args = packForError(
            getString(R.string.msg_server_error), R.drawable.ic_cloud_off_48x48);

        ErrorFragment mErrorFrag = ErrorFragment.getInstance(args);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();
        lp.gravity = Gravity.CENTER;

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frl_TicketsActivity, mErrorFrag)
            .disallowAddToBackStack()
            .commitAllowingStateLoss();

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

    @Override
    public void onItemClick(ServiceRequest theItem) {
        // preview the item which was clicked
        Intent theIntent = new Intent(this, IssueProgressActivity.class);
        Bundle theBundle = new Bundle();
        theBundle.putParcelable(AppConfig.Const.TICKET, theItem);
        theIntent.putExtras(theBundle);
        // bundle the intent
        // start the activity
        startActivity(theIntent);
    }
}
