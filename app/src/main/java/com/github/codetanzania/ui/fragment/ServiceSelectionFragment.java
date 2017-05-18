package com.github.codetanzania.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.codetanzania.adapter.Open311ServiceAdapter;
import com.github.codetanzania.event.ClickListener;
import com.github.codetanzania.event.RecyclerViewTouchListener;
import com.github.codetanzania.model.Open311Service;
import com.github.codetanzania.Constants;

import java.util.List;

import tz.co.codetanzania.R;

public class ServiceSelectionFragment extends Fragment {

    private static final String TAG = "ServiceSelectionFrag";

    public interface OnSelectService {
        void onSelect(Open311Service open311Service);
    }

    private Open311ServiceAdapter mAdapter;

    private OnSelectService mOnSelectService;

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
        mOnSelectService = (OnSelectService) ctx;
    }

    private void bindView(View fragView) {
        List<Open311Service> open311Services = getArguments().getParcelableArrayList(Constants.Const.SERVICE_LIST);
        Log.d(TAG, "List: " + open311Services);
        // recycler view
        RecyclerView rvServices = (RecyclerView) fragView.findViewById(R.id.rv_Services);
        // adapter
        mAdapter = new Open311ServiceAdapter(getActivity(), open311Services);
        // layout manager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        // setup recycler view
        rvServices.setAdapter(mAdapter);
        rvServices.setLayoutManager(layoutManager);

        RecyclerViewTouchListener tListener = new RecyclerViewTouchListener(getActivity(), rvServices, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // select item at a given position
                mAdapter.setSelectedItemIndex(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                // long press allows user to capture picture and fill in information using dialogs
            }
        });

        // add decorations --- API 23+ add support for default dividers
        rvServices.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        // respond to touch event. Basically, we're using
        // gestures to advance cause of action
        rvServices.addOnItemTouchListener(tListener);

        // attach next event listener to button
        attachNextEvent(fragView.findViewById(R.id.btn_OpenIssue));
    }

    private void attachNextEvent(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getSelectedItem() == null) {
                    Toast.makeText(getActivity(), "Please, Select Service", Toast.LENGTH_SHORT).show();
                } else {
                    mOnSelectService.onSelect(mAdapter.getSelectedItem());
                }
            }
        });
    }
}
