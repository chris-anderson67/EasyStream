package cs.tufts.edu.easy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    public static HashMap<Integer, Bathroom> bathroomMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.application_title));
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bathroomMap = new HashMap<>();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent myIntent = new Intent(this, BathroomDetailsActivity.class);
        myIntent.putExtra("bathroom_id", (Integer) marker.getTag());
        startActivity(myIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        String s = null;
        JSONArray jArray = null;
        LatLng sydney = new LatLng(42.408250, -71.120336);

        try {
            /* boolean value determines whether you're requesting all bathrooms data */
            s = new GetData(true).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            jArray = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                Bathroom currBathroom = new Bathroom(oneObject);
                bathroomMap.put(currBathroom.id, currBathroom);
                double lat = currBathroom.latitude;
                double lon = currBathroom.longitude;
                LatLng bathroom = new LatLng(lat, lon);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(bathroom)
                        .title(currBathroom.bathroom_name)
                        .snippet(currBathroom.address));
                marker.setTag(currBathroom.id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        mMap.setOnInfoWindowClickListener(this);
    }
}
