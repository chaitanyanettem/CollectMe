package chaitanya.im.collectme;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AssetDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,
        ResultCallback<LocationSettingsResult> {

    Firebase assetsRef;
    Firebase logsRef;
    Intent intent = new Intent();

    private final String TAG = this.getClass().getName();
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";


    CollapsingToolbarLayout colToolbar;
    Dialog alert;
    EditText edittextalert;
    EditText assetNameEditText;
    EditText assetCategoryEditText;
    EditText assetExtraInfoEditText;
    TextView assetCoordinates;
    TextView assetCoordinatesSaved;
    TextView GPSLabel;
    String assetExtraInfo;
    String assetID;
    String assetName;
    String assetCategory;
    private double latitude;
    private double longitude;
    private double assetLatitude;
    private double assetLongitude;
    CardView logCard;
    AssetLogDataModel logmsg = new AssetLogDataModel("");
    private String currentID;
    private Button button;
    private Boolean savecoordinates = true;
    private Boolean coordinatesAvailable = false;
    AssetListDataModel newAsset;
    FragmentManager fm;
    Switch gpsSwitch;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;

    public static Context context;
    boolean first = false;
    NestedScrollView nestedScrollView;
    AssetListDataModel firebaseAsset;
    FloatingActionButton updateFab;
    Map<String, Object> nameMap = null;
    Map<String, Object> extraMap = null;
    Map<String, Object> categoryMap = null;



    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(AssetDetailActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    public void startUpdatesButtonHandler(View view) {
        checkLocationSettings();
    }

    public void stopUpdatesButtonHandler(View view) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (!coordinatesAvailable) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    mRequestingLocationUpdates = true;
                }

            });
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateLocationUI();
    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        updateLocationUI();
    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            String latlongSt = String.valueOf(latitude) + ", " + longitude;
            assetCoordinates.setText(latlongSt);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }


    @Override
    public void onConnectionSuspended(int arg0) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        assetID = intent.getStringExtra("assetID");
        setContentView(R.layout.activity_asset_detail);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();


        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        colToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        assetNameEditText = (EditText) findViewById(R.id.asset_name);
        assetCategoryEditText = (EditText) findViewById(R.id.asset_category);
        assetExtraInfoEditText = (EditText) findViewById(R.id.asset_extra_info);
        nestedScrollView = (NestedScrollView) findViewById(R.id.item_detail_container);
        assetCoordinates = (TextView) findViewById(R.id.asset_coordinates);
        assetCoordinatesSaved = (TextView) findViewById(R.id.asset_coordinates_saved);
        logCard = (CardView) findViewById(R.id.logCard);
        button = (Button) findViewById(R.id.button);
        gpsSwitch = (Switch) findViewById(R.id.gpsSwitch);
        gpsSwitch.setChecked(true);
        GPSLabel = (TextView) findViewById(R.id.GPSLabel);
        context = this;
        updateFab = (FloatingActionButton) findViewById(R.id.updateFab);
        updateFab.setOnClickListener(new FabOnClickListener());
        alert = createAlert();


        //logCard.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        logsRef = new Firebase("https://flickering-torch-8914.firebaseio.com/logs");

        if (assetID == null){
            button.setVisibility(View.GONE);
            first = true;
            newAsset = new AssetListDataModel("","","","", -1, -1);
        }
        else {
            currentID = assetID;
            assetsRef = new Firebase("https://flickering-torch-8914.firebaseio.com/assets/" + assetID);
            assetName = intent.getStringExtra("assetName");
            assetCategory = intent.getStringExtra("assetCategory");
            assetExtraInfo = intent.getStringExtra("assetExtraInfo");
            assetLatitude = intent.getDoubleExtra("latitude", -1);
            assetLongitude = intent.getDoubleExtra("longitude", -1);

            if (assetLatitude != -1 && assetLongitude != -1) {
                Log.d(TAG, ""+assetLatitude+","+assetLongitude);
                gpsSwitch.setChecked(false);
                gpsSwitch.setVisibility(View.GONE);
                GPSLabel.setVisibility(View.VISIBLE);
                coordinatesAvailable = true;
                assetCoordinates.setVisibility(View.GONE);
                assetCoordinatesSaved.setVisibility(View.VISIBLE);
                assetCoordinatesSaved.setText(""+assetLatitude+", "+assetLongitude);
            }

            assetsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d(TAG, "" + snapshot.toString());
                    firebaseAsset = snapshot.getValue(AssetListDataModel.class);
                    assetName = firebaseAsset.getItemName();
                    assetCategory = firebaseAsset.getCategory();
                    assetExtraInfo = firebaseAsset.getExtraInfo();
                    populateDetails();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }

        assetCategoryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startUpdatesButtonHandler(buttonView);
                    savecoordinates = true;
                } else {
                    stopUpdatesButtonHandler(buttonView);
                    savecoordinates = false;
                }
            }
        });

        button.setOnClickListener(new buttonClickListener());
    }

    class buttonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            alert.show();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(30,0,30,0);
            edittextalert.setLayoutParams(lp);
        }
    }

    class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            assetName = assetNameEditText.getText().toString();
            assetCategory = assetCategoryEditText.getText().toString();
            assetExtraInfo = assetExtraInfoEditText.getText().toString();

            if(assetName.equals("") || assetCategory.equals("")) {
                //Start alert that says that you can't have empty name/category fields.
                Log.d(TAG, "assetName = " + assetName + " assetCategory = " + assetCategory);
                showSnackbar(view, "Can't leave asset name/category empty!");
                return;
            }

            if(first) {
                assetsRef = new Firebase("https://flickering-torch-8914.firebaseio.com/assets/");
                newAsset.itemName = assetName;
                newAsset.category = assetCategory;
                newAsset.extraInfo = assetExtraInfo;
                if (mCurrentLocation != null && savecoordinates) {
                    newAsset.latitude = mCurrentLocation.getLatitude();
                    newAsset.longitude = mCurrentLocation.getLongitude();
                    gpsSwitch.setChecked(false);
                    gpsSwitch.setVisibility(View.GONE);
                    GPSLabel.setVisibility(View.VISIBLE);
                    stopLocationUpdates();
                    assetCoordinates.setVisibility(View.GONE);
                    assetCoordinatesSaved.setVisibility(View.VISIBLE);
                    assetCoordinatesSaved.setText(""+newAsset.latitude+", "+newAsset.longitude);

                }
                Firebase pushRef = assetsRef.push();
                pushRef.setValue(newAsset);
                assetID = pushRef.getKey();
                Map<String, Object> id = new HashMap<>();
                id.put("id", assetID);
                assetsRef = assetsRef.child(assetID);
                assetsRef.updateChildren(id);
                currentID = assetID;
                Log.d(TAG, "FabOnClickListener - first - " + assetName + " " + assetCategory + " " + assetID);
                first = false;
                button.setVisibility(View.VISIBLE);
                showSnackbar(view, "Asset has been created.");
            }
            else {
                Log.d(TAG, "inside fabonclicklistener. updating assetsref children.");

                if (mCurrentLocation != null && savecoordinates) {
                    latitude = mCurrentLocation.getLatitude();
                    longitude = mCurrentLocation.getLongitude();
                    Map <String, Object> mapLatitude = new HashMap<>();
                    Map <String, Object> mapLongitude = new HashMap<>();
                    mapLatitude.put("latitude", latitude);
                    mapLongitude.put("longitude", longitude);
                    gpsSwitch.setChecked(false);
                    gpsSwitch.setVisibility(View.GONE);
                    GPSLabel.setVisibility(View.VISIBLE);
                    stopLocationUpdates();
                    assetCoordinates.setVisibility(View.GONE);
                    assetCoordinatesSaved.setVisibility(View.VISIBLE);
                    assetCoordinatesSaved.setText("" + latitude + ", " + longitude);
                }

                if (!firebaseAsset.getItemName().equals(assetName)) {
                    nameMap = new HashMap<>();
                    nameMap.put("itemName", assetName);
                    assetsRef.updateChildren(nameMap);
                    Log.d(TAG, "nameMap = " + nameMap.toString());
                }
                if (!firebaseAsset.getCategory().equals(assetCategory)) {
                    assetsRef.updateChildren(categoryMap);
                    Log.d(TAG, "categoryMap = "+categoryMap.toString());
                }
                if (!firebaseAsset.getExtraInfo().equals(assetExtraInfo)) {
                    extraMap = new HashMap<>();
                    extraMap.put("extraInfo", assetExtraInfo);
                    assetsRef.updateChildren(extraMap);
                    Log.d(TAG, "extraMap = "+extraMap.toString());
                }
                showSnackbar(view, "Asset updated.");
            }
        }
    }

    public Dialog createAlert() {
        AlertDialog.Builder tempAlert;
        tempAlert = new AlertDialog.Builder(this);

        edittextalert = new EditText(context);
        tempAlert.setTitle("Enter Log");
        tempAlert.setView(edittextalert);
        tempAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logmsg.logMsg = android.text.format.
                        DateFormat.
                        getTimeFormat(context).
                        getDateInstance().
                        format(new Date());

                logmsg.logMsg = logmsg.logMsg + "\n" + edittextalert.getText().toString() + "\n";
                Firebase currentLog = new Firebase("https://flickering-torch-8914.firebaseio.com/logs/" + currentID);
                currentLog.push().setValue(logmsg);
                final TextView logText = (TextView) findViewById(R.id.logText);
                logText.append(logmsg.logMsg);
            }
        });
        tempAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return tempAlert.create();
    }

    private void showCategoryDialog() {
        fm = getSupportFragmentManager();
        CategoryChooser categoryChooser = CategoryChooser.newInstance("Pick category of asset");
        categoryChooser.show(fm, "category_selection");
    }

    private void showSnackbar(View view, String snackbarString) {
        Snackbar snackbar = Snackbar.make(view, snackbarString, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbar.show();
    }

    public void onRadioButtonClicked(View view){

        boolean checked = ((RadioButton) view).isChecked();
        Map<String, Object> category = new HashMap<>();
        String catString = "";

        switch (view.getId()) {
            case R.id.radio_antenna:
                if (checked){
                    category.put("category", "Antenna");
                    catString = "Antenna";
                }
                break;
            case R.id.radio_computer:
                if (checked){
                    category.put("category", "Computer");
                    catString = "Computer";
                }
                break;
            case R.id.radio_meter:
                if (checked){
                    category.put("category", "Meter");
                    catString = "Meter";
                }
                break;
            case R.id.radio_pump:
                if (checked){
                    category.put("category", "Pump");
                    catString = "Pump";
                }
                break;
            case R.id.radio_other:
                if (checked){
                    category.put("category", "Other");
                    catString = "Other";
                }
                break;
        }
        categoryMap = category;
        if(!catString.equals(assetCategoryEditText.getText().toString()))
            if(!updateFab.isShown())
                updateFab.show();
        assetCategoryEditText.setText(catString);
        Fragment prev = getSupportFragmentManager().findFragmentByTag("category_selection");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void populateDetails() {
        colToolbar.setTitle(assetName);
        assetNameEditText.setText(assetName);
        assetCategoryEditText.setText(assetCategory);
        assetExtraInfoEditText.setText(assetExtraInfo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //    finish();
    }


}
