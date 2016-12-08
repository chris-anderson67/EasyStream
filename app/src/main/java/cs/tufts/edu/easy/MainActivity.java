package cs.tufts.edu.easy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.text.Text;

import io.fabric.sdk.android.Fabric;
import java.util.concurrent.ExecutionException;

//import static com.google.android.gms.analytics.internal.zzy.s;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;
    private MainActivity context;
    private LocationManager mLocationManager;
    public static Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        this.mLocationManager = (LocationManager) this.context.getSystemService(this.context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }
        try {
            location = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.v("onMapReady", "trying our best");
            Log.v("onMapReady: latitude", String.valueOf(location.getLatitude()));
            Log.v("onMapReady: longitude", String.valueOf(location.getLongitude()));

        } catch (SecurityException e) {
            e.printStackTrace();
            return;
        }
        if (location == null) {
            return;
        }
    }

    public void onClickFindButton(View view) {
        Intent launchFindIntent = new Intent(MainActivity.this, MapsActivity.class);
        MainActivity.this.startActivity(launchFindIntent);

    }

    public void onClickRateButton(View view) {
        Intent launchRateIntent = new Intent(MainActivity.this, AddActivity.class);
        MainActivity.this.startActivity(launchRateIntent);
//        try {
//            TextView textview = (TextView)findViewById(R.id.outputText);
//            //boolean value determines whether you're requesting all bathrooms data
//            String s = new GetData(true).execute().get();
////            textview.setText(s);
//
//             Toast placeholder = Toast.makeText(this, "Add functionality coming soon!", Toast.LENGTH_LONG);
//             placeholder.show();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
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
