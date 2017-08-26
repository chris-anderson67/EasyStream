package cs.tufts.edu.easy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.constants.IntentKeys;
import cs.tufts.edu.easy.firebase.FirebaseManager;
import cs.tufts.edu.easy.models.Bathroom;

public class LocationAwareActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {


    private static final int MAX_MARKERS = 50;
    private static final float SEARCH_LOCATION_RADIUS_KM = (float) 0.5;
    private static final GeoLocation FALLBACK_LOCATION = new GeoLocation(42.3495,-71.089);

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.application_title));
        setContentView(R.layout.activity_location_aware);

        setupFireBase();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void setupFireBase() {
        bathroomsDatabaseRef = FirebaseManager.getBathroomsReference();
        geoFireDatabaseRef = FirebaseManager.getLocationsReference();
        geoFire = new GeoFire(geoFireDatabaseRef);

        // if no current location yet use fallback location so query is non-null to set listeners
        geoQuery = geoFire.queryAtLocation(currentLocation == null ? FALLBACK_LOCATION : currentLocation,
                SEARCH_LOCATION_RADIUS_KM);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (apiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        buildGoogleApiClient();
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng position = map.getCameraPosition().target;
                geoQuery.setCenter(new GeoLocation(position.latitude, position.longitude));
            }
        });

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(key)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                marker.setTag(key);

                if (markers.size() > MAX_MARKERS) {
                    markers.remove(0).remove(); // remove extra markers from list & map
                }
                markers.add(marker);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All locations loaded for current target");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
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
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent myIntent = new Intent(this, BathroomInfoActivity.class);
        myIntent.putExtra(IntentKeys.BATHROOM_ID, (String) marker.getTag());
        startActivity(myIntent);
    }

    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        apiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.currentLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
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

