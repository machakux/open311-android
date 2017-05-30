package com.github.codetanzania.ui.activity;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import tz.co.codetanzania.R;

public class BaseAppFragmentActivity extends FragmentActivity {


    protected  void setCurrentFragment(int containerId, @NonNull Fragment frag) {
        setCurrentFragment(containerId, frag, true);
    }

    protected void setCurrentFragment(int containerId, @NonNull Fragment frag, boolean shouldAddToBackStack) {
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(containerId, frag)
           .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (shouldAddToBackStack) {
            fts.addToBackStack(null);
        }

        fts.commit();
    }

    protected void showNetworkError(CharSequence msg, CharSequence cancelText, CharSequence confirmText, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(confirmText, onClickListener)
                .setNegativeButton(cancelText, null)
                .create()
                .show();
    }

    protected void showNetworkError(DialogInterface.OnClickListener onClickListener) {
        showNetworkError(getString(R.string.msg_network_error),
                getString(R.string.text_cancel),
                getString(R.string.text_retry),
                onClickListener);
    }

}
