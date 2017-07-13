package cs.tufts.edu.easy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    public static HashMap<Integer, Bathroom> bathroomMap;
    private DatabaseReference ref;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.application_title));
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.geofire_db_path));
        geoFire = new GeoFire(ref);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent myIntent = new Intent(this, BathroomDetailsActivity.class);
        myIntent.putExtra(getString(R.string.bathroom_details_activity_intent_key), (Integer) marker.getTag());
        startActivity(myIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        LatLng defaultLocation = new LatLng(42.408250, -71.120336);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16));
        mMap.setOnInfoWindowClickListener(this);
    }
}
