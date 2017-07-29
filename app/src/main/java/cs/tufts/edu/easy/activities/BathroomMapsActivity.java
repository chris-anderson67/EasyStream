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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.models.Bathroom;
import cs.tufts.edu.easy.constants.IntentKeys;


public class BathroomMapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private String TAG = BathroomMapsActivity.class.getSimpleName();

    private static final int MAX_MARKERS = 20;
    private static final float SEARCH_LOCATION_RADIUS_KM = (float) 0.3;

    private DatabaseReference geoFireDatabaseRef;
    private DatabaseReference bathroomsDatabaseRef;
    private DatabaseReference commentsDatabaseRef;
    private GeoFire geoFire;
    private GeoQuery geoQuery;

    private GeoLocation currentLocation;
    private ArrayList<Marker> markers = new ArrayList<>(20);

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.application_title));
        setContentView(R.layout.activity_maps);

        // Start up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Replace with location manager / listener
        // Get current location from intent
        Intent intent = getIntent();
        currentLocation = new GeoLocation(intent.getDoubleExtra(getString(R.string.maps_intent_latitude), 0),
                                          intent.getDoubleExtra(getString(R.string.maps_intent_longitude), 0));

        // Initialize database connections
        setupFireBase();
    }

    private void setupFireBase() {
        bathroomsDatabaseRef = FirebaseDatabase.getInstance().getReference(getString(R.string.bathrooms_db_path));
        commentsDatabaseRef = FirebaseDatabase.getInstance().getReference(getString(R.string.comments_db_path));
        geoFireDatabaseRef = FirebaseDatabase.getInstance().getReference(getString(R.string.geofire_db_path));
        geoFire = new GeoFire(geoFireDatabaseRef);
        geoQuery = geoFire.queryAtLocation(currentLocation, SEARCH_LOCATION_RADIUS_KM);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
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

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLocation.latitude, currentLocation.longitude), 16));

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
                        .title(key));
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
