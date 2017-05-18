package com.github.codetanzania.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.codetanzania.adapter.OnItemClickListener;
import com.github.codetanzania.adapter.ServiceRequestsAdapter;
import com.github.codetanzania.model.ServiceRequest;
import com.github.codetanzania.util.ServiceRequestsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tz.co.codetanzania.R;

/* Singleton fragment */
public class ServiceRequestsFragment extends Fragment {

    public static final String SERVICE_REQUESTS = "SERVICE_REQUESTS";

    // used by the logcat
    private static final String TAG = "ServiceReqFrag";

    // instance to the click listener will be passed along
    // to the RecyclerView's adapter
    private OnItemClickListener<ServiceRequest> mClickListener;

    private RecyclerView rvTodayServiceRequests;
    private RecyclerView rvYesterdayServiceRequests;
    private RecyclerView rvThisWeekServiceRequests;
    private RecyclerView rvThisMonthServiceRequests;
    private RecyclerView rvOldestServiceRequests;

    // singleton method
    public static ServiceRequestsFragment getNewInstance(Bundle args) {
        ServiceRequestsFragment instance = new ServiceRequestsFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_issue_tickets_group, parent, false);
    }

    @Override public void onViewCreated(
        View view, Bundle savedInstanceState) {
        rvTodayServiceRequests = (RecyclerView)
                view.findViewById(R.id.rv_TodayServiceRequests);

        rvYesterdayServiceRequests = (RecyclerView)
                view.findViewById(R.id.rv_YesterdayServiceRequests);

        rvThisWeekServiceRequests = (RecyclerView)
                view.findViewById(R.id.rv_ThisWeekServiceRequests);

        rvThisMonthServiceRequests = (RecyclerView)
                view.findViewById(R.id.rv_ThisMonthServiceRequests);

        rvOldestServiceRequests = (RecyclerView)
                view.findViewById(R.id.rv_OldMonth);

        SparseArray<ServiceRequest> serviceRequests = getArguments()
                .getSparseParcelableArray(SERVICE_REQUESTS);
        bindServiceRequests(serviceRequests);
    }

    @Override
    public void onAttach(Context theContext) {
        super.onAttach(theContext);
        // cast context... it must implement so!
        if (theContext instanceof OnItemClickListener) {
            mClickListener = (OnItemClickListener<ServiceRequest>) theContext;
        } else {
            throw new IllegalStateException("Attached context must implement OnItemClickListener<T> interface");
        }
    }

    private void bindServiceRequests(
            SparseArray<ServiceRequest> serviceRequests) {

        Log.d(TAG, "=======================SERVICE REQUESTS=========================");
        Log.d(TAG, String.valueOf(serviceRequests));
        Log.d(TAG, "======================/SERVICE REQUESTS=========================");

        List<ServiceRequest> requests = new ArrayList<>(serviceRequests.size());

        for (int i = 0; i < serviceRequests.size(); i++) {
            requests.add(serviceRequests.get(i));
        }

        Map<Integer, List<ServiceRequest>> grouped = ServiceRequestsUtil.group(requests);

        for (Integer key : grouped.keySet()) {
            if (!grouped.get(key).isEmpty()) {
                ServiceRequestsAdapter adapter = new ServiceRequestsAdapter(
                        getActivity(), ServiceRequestsUtil.getI18NTitle(getActivity(), key), grouped.get(key),
                        mClickListener
                );

                RecyclerView recyclerView;

                if (key == ServiceRequestsUtil.TODAY_GROUP) {
                    recyclerView = rvTodayServiceRequests;
                } else if (key == ServiceRequestsUtil.YESTERDAY_GROUP) {
                    recyclerView = rvYesterdayServiceRequests;
                } else if (key == ServiceRequestsUtil.THIS_WEEK_GROUP) {
                    recyclerView = rvThisWeekServiceRequests;
                } else if (key == ServiceRequestsUtil.THIS_MONTH_GROUP) {
                    recyclerView = rvThisMonthServiceRequests;
                } else {
                    recyclerView = rvOldestServiceRequests;
                }

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                        DividerItemDecoration.VERTICAL));
                recyclerView.setHasFixedSize(true);
            }
        }
    }
}
