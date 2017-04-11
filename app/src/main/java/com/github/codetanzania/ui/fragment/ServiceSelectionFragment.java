package com.github.codetanzania.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.codetanzania.adapter.Open311ServiceAdapter;
import com.github.codetanzania.model.Service;
import com.github.codetanzania.util.AppConfig;
import com.github.codetanzania.util.SelectOneObservable;

import java.util.List;

import tz.co.codetanzania.R;

public class ServiceSelectionFragment extends Fragment {

    private static final String TAG = "ServiceSelectionFrag";

    public interface OnServiceSelectionListener {
        int PUBLIC = 0;
        int PRIVATE = 1;
        void selectService(Service service, int serviceScope);
    }

    private OnServiceSelectionListener onServiceSelectionListener;

    // flag to indicate the scope of issue chosen
    private boolean isPublicScope = true;

    public static ServiceSelectionFragment getNewInstance(Bundle args) {
        ServiceSelectionFragment frag = new ServiceSelectionFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_service_selection, group, false);
    }

    @Override public void onViewCreated(View fragView, Bundle savedInstanceState) {
        // bind data to the view
        bindView(fragView);
    }

    @Override public void onAttach(Context ctx) {
        super.onAttach(ctx);
        // make sure the Context where this fragment is attached implements the interface
        // in order to facilitate communication between them
        this.onServiceSelectionListener = (OnServiceSelectionListener) ctx;
    }

    private void bindView(View fragView) {
        List<Service> services = getArguments().getParcelableArrayList(AppConfig.Const.SERVICE_LIST);
        // recycler view
        RecyclerView rvServices = (RecyclerView) fragView.findViewById(R.id.rv_Services);
        // selection helper
        SelectOneObservable<Service> selectionHelper = new SelectOneObservable<>(services);
        // adapter
        Open311ServiceAdapter adapter = new Open311ServiceAdapter(getActivity(), selectionHelper);
        // layout manager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        // setup recycler view
        rvServices.setAdapter(adapter);
        rvServices.setLayoutManager(layoutManager);

        // attach next event listener to button
        attachNextEvent(fragView.findViewById(R.id.btn_NextAction), selectionHelper);

        // attach filter events
        RadioGroup rgSelectOption = (RadioGroup) fragView.findViewById(R.id.rg_SelectOption);
        attachServiceFilterEvent(rgSelectOption, adapter);
    }

    private void attachServiceFilterEvent(RadioGroup radioGroup, final Open311ServiceAdapter adapter) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioBtn_PrivateOption) {
                    adapter.applyFilter(Open311ServiceAdapter.NO_PUBLIC_ISSUES);
                    isPublicScope = false;
                    Toast.makeText(getActivity(), "Showing private issues", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.applyFilter(Open311ServiceAdapter.NO_PRIVATE_ISSUES);
                    isPublicScope = true;
                    Toast.makeText(getActivity(), "Showing public issues", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void attachNextEvent(View button, final SelectOneObservable<Service> selectOneObservable) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onServiceSelectionListener.selectService(selectOneObservable.getSelection(),
                        isPublicScope ? OnServiceSelectionListener.PUBLIC : OnServiceSelectionListener.PRIVATE);
            }
        });
    }
}
