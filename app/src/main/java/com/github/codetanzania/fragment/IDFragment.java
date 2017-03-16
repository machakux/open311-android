package com.github.codetanzania.fragment;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ex.chips.RecipientEditTextView;
import com.github.codetanzania.ServiceGroupItemsActivity;

import tz.co.codetanzania.R;

public class IDFragment extends Fragment {

    /* Used by The Logcat */
    private static final String TAG = "IDFragment";

    /* Permission Code to read Accounts */
    private static final int ACCOUNTS_PERMISSION_CODE = 0;

    /* RecipientEditTextView from https://github.com/klinker41/android-chips. */
    private RecipientEditTextView reEmailTextView;

    /* fragment lifecycle callback. create fragment's view */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        return inflater.inflate(R.layout.frag_id, group, false);
    }

    /* fragment lifecycle callback. attach events */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // let user select email
        /*reEmailTextView = (RecipientEditTextView) view.findViewById(R.id.re_userName);
        reEmailTextView.setTokenizer(new Rfc822Tokenizer());
        final BaseRecipientAdapter reAdapter = new BaseRecipientAdapter(
                BaseRecipientAdapter.QUERY_TYPE_EMAIL, getActivity());
        reAdapter.setShowMobileOnly(false);
        reEmailTextView.dismissDropDownOnItemSelected(true);
        reEmailTextView.setAdapter(reAdapter);*/

        view.findViewById(R.id.btn_Next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ServiceGroupItemsActivity.class));
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
                accounts = manager.getAccounts();
            }
            Log.d(TAG, accounts.toString());
        }
    }
}
