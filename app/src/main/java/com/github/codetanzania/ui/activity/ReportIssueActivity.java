package com.github.codetanzania.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.codetanzania.api.Open311Api;
import com.github.codetanzania.model.Open311Service;
import com.github.codetanzania.ui.fragment.ImageCaptureFragment;
import com.github.codetanzania.ui.fragment.OpenIssueTicketFragment;
import com.github.codetanzania.ui.fragment.ServiceSelectionFragment;
import com.github.codetanzania.util.AppConfig;
import com.github.codetanzania.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.codetanzania.R;

public class ReportIssueActivity extends AppCompatActivity implements
        ServiceSelectionFragment.OnSelectService,
        Callback<ResponseBody>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ImageCaptureFragment.OnStartCapturePhoto,
        OpenIssueTicketFragment.OnPrepareMap,
        OnMapReadyCallback {

    private static final String TAG = "ReportIssueActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /* index of the current selected issue. */
    private static final int FRAG_SELECT_ISSUE_CATEGORY = 0;
    private static final int FRAG_OPEN_ISSUE_TICKET     = 1;
    private static final int FRAG_ISSUE_TICKET          = 2;

    private Fragment frags[] = new Fragment[5];

    // the progress dialog to show while we're loading data
    private ProgressDialog pDialog;

    // the Google client APIs
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    // reference to the views
    private Button mLocationBtn;
    private ImageView mImageView;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        // load stuffs
        // show progress-dialog while we're loading data from the server.
        pDialog = ProgressDialog.show(this, getString(R.string.title_loading_services), getString(R.string.text_loading_services), true);
        // mLocationBtn = (Button) findViewById(R.id.btn_Location);
        // now start load open311Service from the server
        loadServices();
    }

    @Override
    public void onStop() {
        // disconnect from location service if possible
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setCancelable(true)
                        .setMessage(R.string.text_confirm_exit)
                        .setPositiveButton(R.string.action_confirm_exit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.action_cancel_exit, null)
                        .create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadServices() {
        String authHeader = getSharedPreferences(AppConfig.Const.KEY_SHARED_PREFS, MODE_PRIVATE)
                .getString(AppConfig.Const.AUTH_TOKEN, null);
        Call<ResponseBody> call = new Open311Api.ServiceBuilder(this).build(Open311Api.ServicesEndpoint.class)
                .getAll(authHeader);
        call.enqueue(this);
        Log.d(TAG, "----LOADING DATA FROM SRV----");
    }

    // commit the fragment
    private void commitFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frl_FragmentOutlet, frag)
                .commit();
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        // hide the dialog, releasing all the resources
        pDialog.dismiss();

        Log.d(TAG, "Response status: " + response.code());
        Log.d(TAG, "Response message: " + response.message());

        if (response.isSuccessful()) {
            String servicesJson = null;
            try {
                servicesJson = response.body().string();
                List<Open311Service> open311Services = Open311Service.fromJson(servicesJson);
                Log.d(TAG, "Services: " + open311Services);
                // commit the fragment
                // insert fragment in order in which they will appear
                Bundle args = new Bundle();
                args.putParcelableArrayList(AppConfig.Const.SERVICE_LIST, (ArrayList<? extends Parcelable>) open311Services);
                frags[FRAG_SELECT_ISSUE_CATEGORY] = ServiceSelectionFragment.getNewInstance(args);
                frags[FRAG_OPEN_ISSUE_TICKET] = OpenIssueTicketFragment.getNewInstance(null);
                // commit the first fragment
                commitFragment(frags[FRAG_SELECT_ISSUE_CATEGORY]);
            } catch (IOException | JSONException exception) {
                // todo: show an error message. fail safe by telling user we're closing the activity
            }

        } else {
            // todo: show an error message. allow user to reload data if possible
        }
    }

    // when an error occurs
    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        // hide the dialog, releasing all the resources
        Log.e(TAG, String.format("An error was: %s", t.getMessage()));
        Toast.makeText(this, getString(R.string.msg_server_error), Toast.LENGTH_SHORT).show();
        pDialog.dismiss();
        // this is fatal error
        finish();
    }

    // when service is selected
    @Override
    public void onSelect(Open311Service open311Service) {

        // show dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.text_fetch_location));
        pDialog.setIndeterminate(true);
        pDialog.show();

        // fetch last known location which is also the current location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // commit fragment
        commitFragment(frags[FRAG_OPEN_ISSUE_TICKET]);

        // load map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // SupportMapFragment mapFragment = (SupportMapFragment)
        // frags[FRAG_OPEN_ISSUE_TICKET].getChildFragmentManager().findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);
    }

    // the callback to execute when location is retrieved
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // dismiss the dialog
        pDialog.dismiss();

        // fetch location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //mLocationBtn.setText(String.format(Locale.getDefault() ,"(Long: %.2f, Lat: %.2f)",
        //        mLastLocation.getLongitude(), mLastLocation.getLatitude()));
    }

    // the callback to execute when connection fetching is suspended
    @Override
    public void onConnectionSuspended(int i) {
        // dismiss the dialog
        pDialog.dismiss();
        // display a notification
        Toast.makeText(
                this, R.string.text_fetch_location_error, Toast.LENGTH_SHORT).show();
    }

    // the callback to execute when we cannot fetch the location
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // dismiss the dialog
        pDialog.dismiss();
        // display a notification
        Toast.makeText(
                this, R.string.text_fetch_location_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            scaleToPreview();
            addToGallery();
        }
    }

    // the directory where we're going to store captured images.
    private String mCurrentPhotoPath;

    // initiate intent to capture a picture
    private void dispatchTakePictureIntent() {
        Intent capturePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // ensure that there's camera activity to handle the intent
        if ( capturePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = Util.createImageFile(this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ioException) {
                Toast.makeText(
                        this, "An error occur while taking photo.", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the file was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                capturePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                this.startActivityForResult(capturePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // reduce memory footprints by resizing the picture to fit the preview
    private void scaleToPreview() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    // add picture to a list of galleries
    private void addToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Premise Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void startCapture(ImageView mImageView) {
        this.mImageView = mImageView;
        dispatchTakePictureIntent();
    }

    @Override
    public void prepare(SupportMapFragment supportMapFragment) {
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        } else {
            SupportMapFragment smf =
                    new SupportMapFragment();
            // commit the map
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frl_MapView, smf)
                    .disallowAddToBackStack()
                    .commitAllowingStateLoss();
            smf.getMapAsync(this);
        }
    }
}
