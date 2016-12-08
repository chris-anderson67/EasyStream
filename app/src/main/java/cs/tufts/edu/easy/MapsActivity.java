package cs.tufts.edu.easy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    public static HashMap<Integer, Bathroom> bathroomMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;
    // private GoogleApiClient myGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("EasyStream");
        // myGoogleApiClient = new GoogleApiClient.Builder(this);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bathroomMap = new HashMap<>();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.v("INFO WINDOWCLICKED", "CLICKED INFO WINDOW!!");
        Intent myIntent = new Intent(this, Bathroom_Details.class);
        myIntent.putExtra("bathroom_id", (Integer) marker.getTag());
        Log.d("MARKER_TAG: ", Integer.toString((Integer) marker.getTag()));
        startActivity(myIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        String s = null;
        JSONArray jArray = null;
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(42.408250, -71.120336);


        try {
            //boolean value determines whether you're requesting all bathrooms data
            s = new GetData(true).execute().get();
            Log.v("JSONSTRING", s);
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
                Log.v("CURBATHROOM:", currBathroom.bathroom_name);
                Log.v("ID:", Integer.toString(currBathroom.id));
                bathroomMap.put(currBathroom.id, currBathroom);
                Log.v("BATHROOM_INSERTED", currBathroom.bathroom_name);
                // Use for getters/setters
//                int lat = oneObject.getInt("latitude");
//                int lon = oneObject.getInt("longitude");
                double lat = currBathroom.latitude;
                double lon = currBathroom.longitude;
                Log.v("BATHROOM_MAP", String.valueOf(bathroomMap));


                LatLng bathroom = new LatLng(lat, lon);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(bathroom)
                        .title(currBathroom.bathroom_name)
                        .snippet(currBathroom.address));
                marker.setTag(currBathroom.id);


                Log.d("***JOBJECT_lat***", Double.toString(lat));
                Log.d("***JOBJECT_lon***", Double.toString(lon));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        mMap.setOnInfoWindowClickListener(this);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
