package com.github.codetanzania.fragment;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.ex.chips.RecipientEditTextView;
import com.github.codetanzania.IssueTicketGroupsActivity;
import com.github.codetanzania.ServiceGroupItemsActivity;
import com.github.codetanzania.model.Reporter;
import com.github.codetanzania.util.Util;

import tz.co.codetanzania.R;

public class IDFragment extends Fragment {

    /* Used by The Logcat */
    private static final String TAG = "IDFragment";

    /* Permission Code to read Accounts */
    private static final int ACCOUNTS_PERMISSION_CODE = 0;

    /* fragment lifecycle callback. create fragment's view */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.frag_id, group, false);
    }

    /* fragment lifecycle callback. attach events */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        final EditText etUserName = (EditText) view.findViewById(R.id.et_userName);
        final EditText etPhone = (EditText) view.findViewById(R.id.et_phoneNumber);
        view.findViewById(R.id.btn_Next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable phoneNumber = etPhone.getText();
                Editable userName    = etUserName.getText();
                boolean hasError = false;

                if (TextUtils.isEmpty(userName)) {
                    ((TextInputLayout) view.findViewById(R.id.til_UserName)).setError("User name is required");
                    hasError = true;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    ((TextInputLayout) view.findViewById(R.id.til_PhoneNumber)).setError("Phone number is required");
                    hasError = true;
                }

                if (!hasError) {
                    // TODO: start OTP VERIFICATION INSTEAD
                    Reporter reporter = new Reporter();
                    reporter.phone = String.format("%s%s","255",phoneNumber.toString());
                    reporter.name  = userName.toString();
                    Util.storeCurrentReporter(getActivity(), reporter);
                    startActivity(new Intent(getActivity(), IssueTicketGroupsActivity.class));
                }
            }
        });
    }

    /* invoked when the activity has been created */
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestPermissions(new String[]{ Manifest.permission.GET_ACCOUNTS }, ACCOUNTS_PERMISSION_CODE);
    }

    // when request to operate is denied or granted.
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCOUNTS_PERMISSION_CODE) {
            // scan permission
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i]; int result = grantResults[i];
                if (permission == Manifest.permission.GET_ACCOUNTS && result == PackageManager.PERMISSION_GRANTED) {
                    // handle permission
                    loadAccounts();
                } else {
                    // handle rejection
                }
            }
        }
    }

    private void loadAccounts() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            AccountManager manager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
            Account[] accounts = new Account[0];
            accounts = manager.getAccounts();
            Log.d(TAG, accounts.toString());
        }
    }
}
