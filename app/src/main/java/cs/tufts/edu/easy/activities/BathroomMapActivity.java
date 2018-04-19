package cs.tufts.edu.easy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cs.tufts.edu.easy.Constants;
import cs.tufts.edu.easy.ESGeoQueryEventListener;
import cs.tufts.edu.easy.LocationHelper;
import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.firebase.FirebaseManager;
import cs.tufts.edu.easy.models.Bathroom;

public class BathroomMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {


    private static final int MENU_ITEM_ITEM1 = 1;
    private static final int MAX_MARKERS = 50;
    private static final float MAX_SEARCH_LOCATION_RADIUS_KM = (float) 0.7; // km
    private static final int MAX_LOAD_ZOOM_RADIUS = 12; // km
    private static final int DEFAULT_ZOOM_LEVEL = 15;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 10009;
    private static final double DEFAULT_LATITUDE = 40.749213;
    private static final double DEFAULT_LONGITUDE = -73.986392;
    private static final GeoLocation FALLBACK_LOCATION = new GeoLocation(0,0);
    private static final String TAG = BathroomMapActivity.class.getSimpleName();

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private LocationRequest locationRequest;
    private GoogleApiClient apiClient;
    private GeoLocation currentLocation = null;

    private DatabaseReference geoFireDatabaseRef;
    private DatabaseReference bathroomsDatabaseRef;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private ArrayList<Marker> markers = new ArrayList<>(MAX_MARKERS);

    private boolean animatedCurrentLocation = false;

    private FloatingActionButton searchFab;
    private FloatingActionButton newBathroomFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.application_title));
        setContentView(R.layout.activity_location_aware);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setupViews();

        setupFireBase();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "Sign Out");
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case MENU_ITEM_ITEM1:
//
//                return true;
//
//            default:
//                return false;
//        }
//    }

    private void setupViews() {
        searchFab = (FloatingActionButton) findViewById(R.id.search_fab);
        newBathroomFab = (FloatingActionButton) findViewById(R.id.new_bathroom_fab);
        newBathroomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
//            Intent launchRateIntent = new Intent(WelcomeSplashActivity.this, AddBathroomActivity.class);
//            launchRateIntent.putExtra(getString(R.string.maps_intent_latitude), currentLocation.getLatitude());
//            launchRateIntent.putExtra(getString(R.string.maps_intent_longitude), currentLocation.getLongitude());
//            WelcomeSplashActivity.this.startActivity(launchRateIntent);

            }
        });
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(BathroomMapActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(BathroomMapActivity.this, "Problem loading search", Toast.LENGTH_SHORT).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(BathroomMapActivity.this, "Error loading search", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        // Close the app on back pressed
        this.finishAffinity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                CameraPosition newPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(DEFAULT_ZOOM_LEVEL)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition));

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void setupFireBase() {
        bathroomsDatabaseRef = FirebaseManager.getBathroomsReference();
        geoFireDatabaseRef = FirebaseManager.getLocationsReference();
        geoFire = new GeoFire(geoFireDatabaseRef);

        // if no current location yet use fallback location so query is non-null to set listeners
        geoQuery = geoFire.queryAtLocation(currentLocation == null ? FALLBACK_LOCATION : currentLocation,
                MAX_SEARCH_LOCATION_RADIUS_KM);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (apiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        buildGoogleApiClient();

        //noinspection MissingPermission
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);

        // Set default region to be shown if no current location received
        CameraPosition newPosition = new CameraPosition.Builder()
                .target(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE))
                .zoom(DEFAULT_ZOOM_LEVEL)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(newPosition));


        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (LocationHelper.getViewRadius(map) < MAX_LOAD_ZOOM_RADIUS) {
                    LatLng position = map.getCameraPosition().target;
                    geoQuery.setCenter(new GeoLocation(position.latitude, position.longitude));
                }
                // don't update if the user is zoomed far out
            }
        });

        // Add and remove markers as they enter and leave the query radius,
        // remove older markers if limit is reached
        geoQuery.addGeoQueryEventListener(new ESGeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(getString(R.string.loading))
                        .icon(LocationHelper.getMarkerIcon(BathroomMapActivity.this, R.color.colorAccent)));
                marker.setTag(key);

                if (markers.size() > MAX_MARKERS) {
                    markers.remove(0).remove(); // remove extra markers from list & map
                }
                markers.add(marker);
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker == null) {
            return false;
        }
        bathroomsDatabaseRef.child(String.valueOf(marker.getTag()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Bathroom bathroom = dataSnapshot.getValue(Bathroom.class);
                        marker.setTitle(bathroom.name);
                        marker.setSnippet(bathroom.street);
                        marker.showInfoWindow();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent myIntent = new Intent(this, BathroomInfoActivity.class);
        myIntent.putExtra(Constants.IntentKeys.BATHROOM_ID, (String) marker.getTag());
        startActivity(myIntent);
    }

    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .build();
        apiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        if (!animatedCurrentLocation) {
            CameraPosition newPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition));
            animatedCurrentLocation = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}

