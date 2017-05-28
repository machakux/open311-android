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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.codetanzania.Constants;
import com.github.codetanzania.api.Open311Api;
import com.github.codetanzania.model.Open311Service;
import com.github.codetanzania.ui.fragment.ImageCaptureFragment;
import com.github.codetanzania.ui.fragment.JurisdictionsBottomSheetDialogFragment;
import com.github.codetanzania.ui.fragment.OpenIssueTicketFragment;
import com.github.codetanzania.ui.fragment.ServiceSelectionFragment;
import com.github.codetanzania.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tz.co.codetanzania.R;

public class ReportIssueActivity extends AppCompatActivity implements
        ServiceSelectionFragment.OnSelectService,
        OpenIssueTicketFragment.OnSelectAddress,
        OpenIssueTicketFragment.OnPostIssue,
        Callback<ResponseBody>,
        JurisdictionsBottomSheetDialogFragment.OnAcceptAddress,
        ImageCaptureFragment.OnStartCapturePhoto {

    private static final String TAG = "ReportIssueActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 2;

    /* index of the current selected issue. */
    private static final int FRAG_SELECT_ISSUE_CATEGORY = 0;
    private static final int FRAG_OPEN_ISSUE_TICKET     = 1;
    private static final int FRAG_ISSUE_TICKET          = 2;

    private Fragment frags[] = new Fragment[5];

    // the progress dialog to show while we're loading data
    private ProgressDialog pDialog;
    private JurisdictionsBottomSheetDialogFragment jbsDialog;
    private ImageView mImageView;
    // location
    private Map<String, Double[]> mLocationMap;
    // address
    private String mLocationAddress;
    // Service
    private String mServiceId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        // load stuffs
        loadServices();

        // request permission to read location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // case when user never selected "Never allow" but he/she still declined the request
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                displayDialogForPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        getString(R.string.text_allow_location_access),
                        getString(R.string.action_confirm_access_location),
                        getString(R.string.action_decline_access_location));
            }
            // let's assume we can ask for permission anyway
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }
        }
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
        String authHeader = getSharedPreferences(Constants.Const.KEY_SHARED_PREFS, MODE_PRIVATE)
                .getString(Constants.Const.AUTH_TOKEN, null);
        Call<ResponseBody> call = new Open311Api.ServiceBuilder(this).build(Open311Api.ServicesEndpoint.class)
                .getAll(authHeader);
        call.enqueue(this);
        Log.d(TAG, "----LOADING DATA FROM SRV----");
        // show progress-dialog while we're loading data from the server.
        pDialog = ProgressDialog.show(this, getString(R.string.title_loading_services), getString(R.string.text_loading_services), true);
    }

    private void displayDialogForPermission(String manifestPermissionId, String message, String pButtonText, String nButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
        .setPositiveButton(pButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ask for permission
                ActivityCompat.requestPermissions(ReportIssueActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            }
        })
        .setNegativeButton(nButtonText, null);
        builder.create().show();
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

                Log.d(TAG, servicesJson);

                List<Open311Service> open311Services = Open311Service.fromJson(servicesJson);
                // Log.d(TAG, "Services: " + open311Services);
                // commit the fragment
                // insert fragment in order in which they will appear
                Bundle args = new Bundle();
                args.putParcelableArrayList(Constants.Const.SERVICE_LIST, (ArrayList<? extends Parcelable>) open311Services);
                frags[FRAG_SELECT_ISSUE_CATEGORY] = ServiceSelectionFragment.getNewInstance(args);
                frags[FRAG_OPEN_ISSUE_TICKET] = OpenIssueTicketFragment.getNewInstance(null);
                // commit the first fragment
                commitFragment(frags[FRAG_SELECT_ISSUE_CATEGORY]);
            } catch (IOException | JSONException exception) {
                Toast.makeText(this, "Error Processing Data", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {
            Toast.makeText(this, "Invalid Request/Response", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // when activity result is received back
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int grantResults[]) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // we can ask for permission
                }
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
        // note the service id
        mServiceId = open311Service.id;
        // commit fragment
        commitFragment(frags[FRAG_OPEN_ISSUE_TICKET]);
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

    // open bottom sheet. Used to collect address
    private void openLocationBottomSheet(Bundle bundle) {
        if (jbsDialog == null) {
            jbsDialog =
                    JurisdictionsBottomSheetDialogFragment.getNewInstance(bundle);
            jbsDialog.setCancelable(false);
        } else {
            jbsDialog.setArguments(bundle);
        }

        jbsDialog.show(getSupportFragmentManager(), jbsDialog.getTag());
    }

    @Override
    public void startCapture(ImageView mImageView) {
        this.mImageView = mImageView;
        dispatchTakePictureIntent();
    }

    @Override
    public void selectAddress(Bundle args) {
        openLocationBottomSheet(args);
    }

    @Override
    public void selectedAddress(Bundle locationData) {
        // now post the issue to the server
        Location location = locationData.getParcelable(Constants.LOCATION_DATA_EXTRA);
        mLocationAddress = locationData.getString(Constants.RESULT_DATA_KEY);
        // Location
        mLocationMap = new HashMap<>();
        mLocationMap.put("coordinates", new Double[]{ location.getLongitude(), location.getLatitude() });
    }

    @Override
    public void doPost(Map<String, Object> issueMap) {
        // first thing first, check if user has provided location details
        if (mLocationMap.isEmpty()) {
            Toast.makeText(this, R.string.warning_empty_issue_location, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mServiceId)) {
            Toast.makeText(this, R.string.warning_empty_service_id, Toast.LENGTH_SHORT).show();
            return;
        }

        issueMap.put("location", mLocationMap);

        // pack location address if it's available
        if (!TextUtils.isEmpty(mLocationAddress)) {
            issueMap.put("address", mLocationAddress);
        }

        // pack service id
        issueMap.put("service", mServiceId);

        // Prepare the dialog
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.text_opening_ticket));
        dialog.setIndeterminate(true);


        // do the posting
        new Open311Api.ServiceBuilder(this).build(Open311Api.ServiceRequestEndpoint.class)
                .openTicket("Bearer " + Util.getAuthToken(this),issueMap)
                .enqueue(getPostIssueCallback(dialog));

        // show the dialog
        dialog.show();
    }

    private void displayMessage(String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Received Ticket ID: " + code);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    private Callback<ResponseBody> getPostIssueCallback(final ProgressDialog dialog) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    // TODO: Get issue Ticket and display it to the user
                    try {
                        String str = response.body().string();
                        JSONObject jsonObject = new JSONObject(str);
                        String code = jsonObject.getString("code");
                        displayMessage(code);
                    } catch (IOException | JSONException ioException) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                // show error message
                Toast.makeText(ReportIssueActivity.this, R.string.msg_network_error, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
