package com.github.codetanzania.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

import tz.co.codetanzania.R;

public class OpenIssueTicketFragment extends Fragment {

    public static final String TAG = "OITF";

    public interface OnPrepareMap {
        void prepare(SupportMapFragment supportMapFragment);
    }

    private OnPrepareMap onPrepareMap;

    public static OpenIssueTicketFragment getNewInstance(Bundle args) {
        OpenIssueTicketFragment frag = new OpenIssueTicketFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override public void onAttach(Context mContext) {
        super.onAttach(mContext);
        this.onPrepareMap = (OnPrepareMap) mContext;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportMapFragment smf = new SupportMapFragment();
        this.onPrepareMap.prepare(smf);
    }

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_open_issue_ticket, group, false);
    }
}
