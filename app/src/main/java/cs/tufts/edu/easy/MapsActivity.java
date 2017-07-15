package cs.tufts.edu.easy;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    /* remove */
    public static HashMap<Integer, Bathroom> bathroomMap;

    private GeoLocation currentLocation;
    private DatabaseReference ref;
    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private ArrayList<Marker> markers;
    private static final int MAX_MARKERS = 20;
    private static final float SEARCH_LOCATION_RADIUS_KM = (float) 0.3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.application_title));
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get current location from intent
        Intent intent = getIntent();
        currentLocation = new GeoLocation(intent.getDoubleExtra(getString(R.string.maps_intent_latitude), 0),
                                          intent.getDoubleExtra(getString(R.string.maps_intent_longitude), 0));

        setupGeoFire();
        markers = new ArrayList<>(20);
    }

    private void setupGeoFire() {
        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.geofire_db_path));
        geoFire = new GeoFire(ref);
        geoQuery = geoFire.queryAtLocation(currentLocation, SEARCH_LOCATION_RADIUS_KM);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent myIntent = new Intent(this, BathroomDetailsActivity.class);
        myIntent.putExtra(getString(R.string.bathroom_details_activity_intent_key), (Integer) marker.getTag());
        startActivity(myIntent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(key));

                if (markers.size() > MAX_MARKERS) {
                    Marker toDelete = markers.remove(0);
                    toDelete.remove();
                }
                markers.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLocation.latitude, currentLocation.longitude), 16));
        googleMap.setOnInfoWindowClickListener(this);


        // Lazy - refresh bathroom locations when the camera has stopped moving
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng position = googleMap.getCameraPosition().target;
                geoQuery.setCenter(new GeoLocation(position.latitude, position.longitude));
            }
        });

    }



}
