package org.taitasciore.android.fmtest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.taitasciore.android.storage.StationsContract;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements MainView,
        LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback{

    private static final float DEFAULT_ZOOM = 14f;

    boolean mIsGoogleApiAvailable = true;
    boolean mIsPositionStored = false;
    boolean mAddedMarkers = false;
    double lat;
    double lon;
    float zoom = DEFAULT_ZOOM;

    List<ApiResponse.Metro> stations = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();

    GoogleMap mGoogleMap;

    Menu menu;
    TextView tvSorry;

    WorkerFragment mWorkerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startWorkerFragment();
        checkForGoogleApiAvailability();

        tvSorry = (TextView) findViewById(R.id.tvSorry);

        /**
         * Retrieves saved position (last position before orientation change/rotation)
         * and zoom level
         */
        if (savedInstanceState != null) {
            lat  = savedInstanceState.getDouble("lat");
            lon  = savedInstanceState.getDouble("lon");
            zoom = savedInstanceState.getFloat("zoom");
            mIsPositionStored = true;
        }
    }

    private void startWorkerFragment() {
        mWorkerFragment = (WorkerFragment)
                getSupportFragmentManager().findFragmentByTag("worker");

        /**
         * This will yield true when opening the activity. After that, instantiation
         * of fragment will not be neccesary because we're dealing with a retained fragment
         */
        if (mWorkerFragment == null) {
            mWorkerFragment = new WorkerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(mWorkerFragment, "worker").commit();
        }
    }

    /**
     * Used by the Calligraphy library
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * I added a simple search feature though this was not part of the
     * project specification/requirements, but I figured it would be a nice asset which also helped
     * me learn something new and fun
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu; // Stores menu for later reference to get SearchView
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Only query ContentProvider if the query contains more than one character
                // and Google Play Services is available for use
                if (newText.length() > 1 && mIsGoogleApiAvailable)
                    mWorkerFragment.getPresenter().searchSuggestions(newText);
                return true;
            }
        });

        return true;
    }

    /**
     * Sets every marker's color to the default one
     */
    private void resetMarkersColor() {
        for (Marker m : markers)
            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private Marker findMarker(int id) {
        for (Marker m : markers)
            if ((int) m.getTag() == id)
                return m;
        return null;
    }

    /**
     * Determines whether Google Play Services is available for use on the device.
     * If not, a {@link Dialog} is shown with the error message. This was implemented in this way
     * because, though the app will not crash because the system will handle things by us,
     * a rather ugly message is shown directly in the screen in a {@link TextView}.
     * It is better to show the error in a dialog. If Google Play Services is available,
     * the {@link SupportMapFragment} will be started asynchronously for later use
     */
    private void checkForGoogleApiAvailability() {
        GoogleApiAvailability mApiAvailability = GoogleApiAvailability.getInstance();
        int mResultCode = mApiAvailability.isGooglePlayServicesAvailable(this);

        if (mResultCode == ConnectionResult.SUCCESS)
            showMap();
        else {
            mIsGoogleApiAvailable = false;
            showGoogleApiError(mApiAvailability.getErrorDialog(this, mResultCode, 001));
        }
    }

    /**
     * This method starts the {@link SupportMapFragment}  asynchronosly
     */
    private void showMap() {
        SupportMapFragment mMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentByTag("map");

        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);
        else {
            mMapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mMapFragment, "map").commit();
            mMapFragment.setRetainInstance(true);
            mMapFragment.getMapAsync(this);
        }
    }

    /**
     * This method is responsible for showing the appropiate error in case Google Play Services
     * is not available for use in the device for whatever reason. This could happen for several
     * reasons i.e. Google Play Services is not installed on the device, is disabled, needs to be
     * updated, etc
     * @param mErrorDialog The error message to show in the dialog
     */
    private void showGoogleApiError(Dialog mErrorDialog) {
        mErrorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showSorryText();
            }
        });
        mErrorDialog.show();
    }

    /**
     * This method saves the current position in the map, as well as the level of zoom
     * so the map can be restored to its previous state after orientation change/rotation
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mGoogleMap != null) {
            LatLng pos = mGoogleMap.getCameraPosition().target;
            outState.putDouble("lat", pos.latitude);
            outState.putDouble("lon", pos.longitude);
            outState.putFloat("zoom", mGoogleMap.getCameraPosition().zoom);
        }
    }

    /**
     * A {@link CursorLoader} is setup, which will retrieve the cursor in the onLoadFinished method
     * @param id
     * @param args
     * @return Cursor with results, if any. Null otherwise
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader mCursorLoader = new CursorLoader(
                this, StationsContract.CONTENT_URI, null, null, null, null);
        return mCursorLoader;
    }

    /**
     * Adds markers to the map if there are stations stored in the
     * {@link android.content.ContentProvider}. If not, an HTTP request will be started by the
     * {@link MainPresenter} to retrieve the array of stations from the API
     * @param loader The {@link Loader}
     * @param cursor The {@link Cursor} with results, if any
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ApiResponse.Metro metro = Utils.buildStation(cursor);
                addMarker(metro);
                stations.add(metro);
            } while (cursor.moveToNext());
        }
        else
            mWorkerFragment.getPresenter().getSubwayData();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * {@link GoogleMap} is ready for use, so an
     * {@link com.google.android.gms.maps.GoogleMap.OnMarkerClickListener} is setup on the
     * {@link GoogleMap} instance.
     * @param googleMap {@link GoogleMap} instance
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mWorkerFragment.getPresenter().searchStationById((int) marker.getTag());
                return true;
            }
        });

        // Start loader. It is started here to make sure that the map will be available
        // when the Loader's onLoadFinished method is called
        getSupportLoaderManager().initLoader(001, null, this);
    }

    /**
     * This method adds a marker to the map for the given station
     * @param m {@link org.taitasciore.android.fmtest.ApiResponse.Metro} instance with its
     * information so that its corresponding marker can be added
     */
    private void addMarker(ApiResponse.Metro m) {
        Log.i("debug", "adding marker");
        // Getting station coordinates
        LatLng coords = new LatLng(m.getLat(), m.getLon());
        // Setting up marker and adding it to the map
        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(coords)
                .title(m.getName() + " (" + m.getLine() + ")")
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED)));
        // Adding tag to marker so it can be retrived later if it's clicked on
        marker.setTag(m.getId());
        markers.add(marker);

        if (m.getName().equalsIgnoreCase("catalunya")) {
            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(coords, zoom);
            //mGoogleMap.animateCamera(mCameraUpdate, 3000, null);
            mGoogleMap.moveCamera(mCameraUpdate);
        }
    }

    /**
     * This method shows a {@link ProgressDialog} with a message to let the user know
     * there's a background task in process
     */
    @Override
    public void showNetworkProgress() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ProgressDialogFragment f = new ProgressDialogFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(f, "progress").commitAllowingStateLoss();
                getSupportFragmentManager().executePendingTransactions();
            }
        };
        handler.post(runnable);
    }

    /**
     * The {@link ProgressDialog} is dismissed because background tasks finished
     */
    @Override
    public void hideProgress() {
        ProgressDialogFragment f = (ProgressDialogFragment)
                getSupportFragmentManager().findFragmentByTag("progress");
        if (f != null) f.dismiss();
    }

    @Override
    public void showSorryText() {
        tvSorry.setVisibility(View.VISIBLE);
    }

    /**
     * This method sets up a {@link SuggestionCursorAdapter} to show suggestions
     * retrieved by the {@link android.content.ContentProvider} for the given query
     * @param c {@link Cursor} retrived by the {@link android.content.ContentProvider} with the
     * results
     */
    @Override
    public void showSuggestions(Cursor c) {
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        if (c != null && c.moveToFirst()) {
            final SuggestionCursorAdapter mAdapter = new SuggestionCursorAdapter(this, c);

            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    // Hide/close keyboard
                    Utils.hideKeyboard(MainActivity.this);
                    Cursor cursor = mAdapter.getCursor();
                    resetMarkersColor();
                    ApiResponse.Metro metro = Utils.buildStation(cursor);
                    moveCameraSlowly(metro);

                    return true;
                }
            });

            searchView.setSuggestionsAdapter(mAdapter);
        }
    }

    @Override
    public void showStationInfo(ApiResponse.Metro station) {
        if (station != null) Utils.showStationInfoDialog(this, station);
    }

    @Override
    public void showNetworkError() {
        Utils.toast(this, "Check your internet connection");
    }

    /**
     * Moves the camera slowly to the coordinates of the given station
     * @param metro {@link org.taitasciore.android.fmtest.ApiResponse.Metro} instance
     * whose coordinates will be extracted to move the camera slowly to the corresponding position
     */
    private void moveCameraSlowly(ApiResponse.Metro metro) {
        double lat = metro.getLat();
        double lon = metro.getLon();
        LatLng coords = new LatLng(lat, lon);

        if (mGoogleMap != null) {
            zoom = mGoogleMap.getCameraPosition().zoom;
            CameraUpdate mCameraUpdate =
                CameraUpdateFactory.newLatLngZoom(coords, zoom);
            mGoogleMap.animateCamera(mCameraUpdate, 1000, null);
            Marker marker = findMarker(metro.getId());
            if (marker != null) changeMarkerColor(marker);
        }
    }

    private void changeMarkerColor(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE));
    }
}