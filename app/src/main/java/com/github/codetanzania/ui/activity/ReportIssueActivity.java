package com.github.codetanzania.ui.activity;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.github.codetanzania.model.Service;
import com.github.codetanzania.ui.fragment.ServiceSelectionFragment;
import com.github.codetanzania.util.AppConfig;

import tz.co.codetanzania.R;

public class ReportIssueActivity extends FragmentActivity implements ServiceSelectionFragment.OnServiceSelectionListener {

    /* index of the current selected issue. */
    private static final int FRAG_SELECT_ISSUE_CATEGORY = 0;
    private static final int FRAG_REPORT_PRIVATE_ISSUE  = 1;
    private static final int FRAG_REPORT_PUBLIC_ISSUE   = 2;
    // private static final int FRAG_TAKE_PHOTO         = 3;
    // private static final int FRAG_RECORD_AUDIO       = 4;
    private static final int FRAG_PRIVATE_ISSUE_SUMMARY = 3;
    private static final int FRAG_PUBLIC_ISSUE_SUMMARY  = 4;

    Fragment frags[] = new Fragment[5];

    // Instance to the selected service
    private Service mSelectedService;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // insert fragment in order in which they will appear
        frags[FRAG_SELECT_ISSUE_CATEGORY] = ServiceSelectionFragment.getNewInstance(
                getIntent().getBundleExtra(AppConfig.Const.SERVICE_LIST));
        // commit the first fragment
        commitFragment(frags[FRAG_SELECT_ISSUE_CATEGORY]);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: check if user may exit
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void selectService(Service service, int serviceScope) {
        if (serviceScope == ServiceSelectionFragment.OnServiceSelectionListener.PUBLIC) {
            // commit ReportPublicIssueFragment
            commitFragment(frags[FRAG_REPORT_PUBLIC_ISSUE]);
        } else {
            // commit ReportPrivateIssueFragment
            commitFragment(frags[FRAG_REPORT_PRIVATE_ISSUE]);
        }
        mSelectedService = service;
    }

    private void commitFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frl_FragmentOutlet, frag)
                .commit();
    }
}
