package com.github.codetanzania.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

import com.github.codetanzania.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import tz.co.codetanzania.R;

public class JurisdictionsBottomSheetDialogFragment extends BottomSheetDialogFragment implements OnMapReadyCallback {

    private SupportMapFragment mMapFrag;
    private MaterialBetterSpinner mSpinner;

    private Location mLocation;
    private String   mAddress;

    public static JurisdictionsBottomSheetDialogFragment getNewInstance(Bundle args) {
        JurisdictionsBottomSheetDialogFragment instance = new JurisdictionsBottomSheetDialogFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override public void onResume() {
        super.onResume();

        mLocation = getArguments().getParcelable(Constants.LOCATION_DATA_EXTRA);
        mAddress  = getArguments().getString(Constants.RESULT_DATA_KEY);

        if (mLocation != null && mAddress != null) {
            // todo hide mSpinner
            mMapFrag = (SupportMapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mMapFrag.getMapAsync(this);
        } else {
            // render form
        }
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.frag_location_content, null);
        dialog.setContentView(contentView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // show location

    }
}
