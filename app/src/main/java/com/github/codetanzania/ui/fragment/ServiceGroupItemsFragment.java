package com.github.codetanzania.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.github.codetanzania.adapter.ServiceGroupItemsAdapter;
import com.github.codetanzania.model.Jurisdiction;
import com.github.codetanzania.model.LongLat;
import com.github.codetanzania.model.Open311Service;
import com.github.codetanzania.model.ServiceGroup;

import java.util.ArrayList;
import java.util.List;

import tz.co.codetanzania.R;
/* singleton fragment */
public class ServiceGroupItemsFragment extends Fragment {

    // singleton method
    private static ServiceGroupItemsFragment mServiceGroupItemsFragment;

    public static ServiceGroupItemsFragment getInstance(Bundle args) {

        // only initialize when the instance was destroyed
        if (mServiceGroupItemsFragment == null) {
            mServiceGroupItemsFragment = new ServiceGroupItemsFragment();
            mServiceGroupItemsFragment.setArguments(args);
        }

        return mServiceGroupItemsFragment;
    }

    @Override public android.view.View onCreateView(
            LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_service_group_items, viewGroup, false);
    }

    @Override public void onViewCreated(
            View view, Bundle savedInstanceState) {
        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(
                R.id.elv_serviceGroupItems);
        ServiceGroupItemsAdapter serviceGroupItemsAdapter = new ServiceGroupItemsAdapter(
                getActivity(), initService());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            expandableListView.expandGroup(0, true);
        }

        expandableListView.setAdapter(serviceGroupItemsAdapter);
    }

    private List<ServiceGroup> initService() {
        List<ServiceGroup> groups = new ArrayList<>();
        ServiceGroup group = new ServiceGroup();
        group.name = "DAWASCO";

        Jurisdiction jurisdiction = new Jurisdiction();

        jurisdiction.name = "Dar-es-Salaam";
        jurisdiction.code = "DAWASCO-HQ";
        jurisdiction.location = new LongLat(11.0, 23.0);
        jurisdiction.domain = "tz.co.codetanzania";

        group.jurisdiction = jurisdiction;
        group.open311Services = new ArrayList<>();

        Open311Service open311Service = new Open311Service();
        open311Service.name = "Water Leakage";
        open311Service.description = "Report Water leakage. Can be due to vandalism or faulty infrastructure";
        open311Service.code = "WL";
        group.open311Services.add(open311Service);

        open311Service = new Open311Service();
        open311Service.name = "Lack of Water";
        open311Service.description = "Report Lack of Water / Shortage of water";
        open311Service.code = "LW";
        group.open311Services.add(open311Service);

        open311Service = new Open311Service();
        open311Service.name = "Water Theft";
        open311Service.description = "Report Water Theft";
        open311Service.code = "WT";
        group.open311Services.add(open311Service);

        open311Service = new Open311Service();
        open311Service.name = "Dirty Water";
        open311Service.description = "Report Dirty/ Untreated water";
        open311Service.code = "DW";
        group.open311Services.add(open311Service);

        groups.add(group);

        return groups;
    }
}
