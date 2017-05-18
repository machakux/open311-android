package com.github.codetanzania.ui.fragment;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;

import com.github.codetanzania.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import tz.co.codetanzania.R;

public class JurisdictionsBottomSheetDialogFragment extends BottomSheetDialogFragment implements OnMapReadyCallback {

    private static final String TAG = "JBSD";

    private SupportMapFragment mMapFrag;
    private MaterialBetterSpinner mSpinner;

    private GoogleMap mMap;

    private Location mLocation;
    private String   mAddress;

    private BottomSheetBehavior.BottomSheetCallback behaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            // we need this or the app will crash when user decides to change location
            if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                if (mMapFrag != null) {
                    getFragmentManager().beginTransaction()
                            .remove(mMapFrag)
                            .disallowAddToBackStack()
                            .commitAllowingStateLoss();
                }

                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    public void selectLocation(View view) {

    }

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
        // noinspection RestrictedApi
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.frag_location_content, null);

        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)((View)contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        Log.d(TAG, "Behavior: " + behavior);

        if ( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(behaviorCallback);
        }
    }

    @Override public void onViewCreated(View contentView, Bundle savedInstanceState) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // show location
        mMap = googleMap;
        LatLng coords = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(coords).title(mAddress));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coords)
                .zoom(12)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
