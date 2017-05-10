package com.github.codetanzania.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tz.co.codetanzania.R;

public class OpenIssueTicketFragment extends Fragment {

    public static OpenIssueTicketFragment getNewInstance(Bundle args) {
        OpenIssueTicketFragment frag = new OpenIssueTicketFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_open_issue_ticket, group, false);
    }
}
