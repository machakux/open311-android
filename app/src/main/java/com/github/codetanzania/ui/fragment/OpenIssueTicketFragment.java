package com.github.codetanzania.ui.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.codetanzania.Constants;
import com.github.codetanzania.service.FetchAddressIntentService;
import com.github.codetanzania.ui.activity.ReportIssueActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Locale;

import tz.co.codetanzania.R;

public class OpenIssueTicketFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "OITF";

    // the Google client APIs
    private GoogleApiClient mGoogleApiClient;

    // flag used to resolve async conflicts
    private boolean mAddressRequested = false;

    private TextView mTextViewCurrentLocation;
    private Button mBtnGetLocation;
    private ProgressDialog pDialog;
    private Location mLastLocation;
    private AddressResultReceiver mAddressReceiver;

    public interface OnSelectAddress {
        void selectAddress(Bundle args);
    }

    private OnSelectAddress onSelectAddress;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onSelectAddress.selectAddress(null);
            return;
        }

        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        // make sure the location was found by activity before starting fetch-address-service
        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(getActivity(), R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mAddressRequested) {
                startFetchAddressService();
            }
        }
    }

    public void fetchAddressButtonHandler(View view) {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startFetchAddressService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets(true);
    }

    // update widgets
    private void updateUIWidgets(boolean requesting) {
        mBtnGetLocation.setEnabled(!requesting);
        if (requesting) {
            mBtnGetLocation.setText(R.string.text_fetching_location);
        } else {
            mBtnGetLocation.setText(R.string.text_select_location);
        }
    }

    private void startFetchAddressService() {
        mAddressReceiver = new AddressResultReceiver(new Handler());
        Intent fetchAddressIntent = new Intent(getActivity(), FetchAddressIntentService.class);
        fetchAddressIntent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        fetchAddressIntent.putExtra(Constants.RECEIVER, mAddressReceiver);
        getActivity().startService(fetchAddressIntent);

        // show dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.text_fetching_location));
        pDialog.setIndeterminate(true);
        pDialog.show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended.");
        pDialog.dismiss();
        onSelectAddress.selectAddress(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect.");
        pDialog.dismiss();
        onSelectAddress.selectAddress(null);
    }


    public static OpenIssueTicketFragment getNewInstance(Bundle args) {
        OpenIssueTicketFragment frag = new OpenIssueTicketFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override public void onAttach(Context mContext) {
        super.onAttach(mContext);
        onSelectAddress = (OnSelectAddress) mContext;
    }

    @Override public void onStop() {
        // disconnect from location service if possible
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fetch last known location which is also the current location
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override public void onViewCreated(View fragView, Bundle savedInstanceState) {
        this.mTextViewCurrentLocation = (TextView) fragView.findViewById(R.id.tv_CurrentLocation);
        this.mTextViewCurrentLocation.setText(R.string.text_empty_location);
        this.mBtnGetLocation = (Button) fragView.findViewById(R.id.btn_GetLocation);
        this.mBtnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAddressButtonHandler(v);
            }
        });
    }

    @Override public View onCreateView(
            LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_open_issue_ticket, group, false);
    }

    private void displayAddressOutput(Bundle data) {
        String address = data.getString(Constants.RESULT_DATA_KEY);
        String strData = getString(R.string.text_curr_location, address,
                String.format(Locale.getDefault(),"%.2f", mLastLocation.getLatitude()),
                String.format(Locale.getDefault(), "%.2f", mLastLocation.getLongitude()));
        mTextViewCurrentLocation.setText(strData);
        data.putParcelable(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        onSelectAddress.selectAddress(data);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                pDialog.dismiss();
                updateUIWidgets(false);
                displayAddressOutput(resultData);
                Toast.makeText(getActivity(), R.string.address_found, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.text_fetch_location_error, Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
                updateUIWidgets(false);
            }
        }
    }
}
