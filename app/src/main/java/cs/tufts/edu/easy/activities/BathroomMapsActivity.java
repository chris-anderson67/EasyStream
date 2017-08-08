package cs.tufts.edu.easy.activities;

import android.content.Intent;
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

import static cs.tufts.edu.easy.R.id.map;


public class BathroomMapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private String TAG = BathroomMapsActivity.class.getSimpleName();

    private static final int MAX_MARKERS = 50;
    private static final float SEARCH_LOCATION_RADIUS_KM = (float) 0.5;
    private static final String CURRENT_LOCATION_MARKER = "current_location_marker";

    private DatabaseReference geoFireDatabaseRef;
    private DatabaseReference bathroomsDatabaseRef;
    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private GeoLocation currentLocation;
    private ArrayList<Marker> markers = new ArrayList<>(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.application_title));
        setContentView(R.layout.activity_maps);

        // Start up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        // Replace with currentLocation manager / listener
        // Get current currentLocation from intent
        Intent intent = getIntent();
        currentLocation = new GeoLocation(intent.getDoubleExtra(getString(R.string.maps_intent_latitude), 0),
                                          intent.getDoubleExtra(getString(R.string.maps_intent_longitude), 0));

        // Initialize database connections
        setupFireBase();
    }

    private void setupFireBase() {
        bathroomsDatabaseRef = FirebaseManager.getBathroomsReference();
        geoFireDatabaseRef = FirebaseManager.getLocationsReference();
        geoFire = new GeoFire(geoFireDatabaseRef);
        geoQuery = geoFire.queryAtLocation(currentLocation, SEARCH_LOCATION_RADIUS_KM);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.getTag().equals(CURRENT_LOCATION_MARKER)) {
            marker.showInfoWindow();
            return true;
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
        if (marker.getTag().equals(CURRENT_LOCATION_MARKER)) {
            return;
        }
        Intent myIntent = new Intent(this, BathroomInfoActivity.class);
        myIntent.putExtra(IntentKeys.BATHROOM_ID, (String) marker.getTag());
        startActivity(myIntent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));


        // Current location
        LatLng currentLatLng = new LatLng(currentLocation.latitude, currentLocation.longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
                .setTag(CURRENT_LOCATION_MARKER);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));

        // update the target for the geoquery when the camera stops
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng position = googleMap.getCameraPosition().target;
                geoQuery.setCenter(new GeoLocation(position.latitude, position.longitude));
            }
        });

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(key)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                marker.setTag(key);

                if (markers.size() > MAX_MARKERS) {
                    markers.remove(0).remove();
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

}
