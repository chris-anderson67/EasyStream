package cs.tufts.edu.easy;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class WelcomeSplashActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;
    private WelcomeSplashActivity context;
    private LocationManager mLocationManager;
    public static Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        location = new Location("");
        location.setLatitude(42.408250);
        location.setLongitude(-71.120336);

        /* GET CURRENT LOCATION - replace with static location for now */
//        this.mLocationManager = (LocationManager) this.context.getSystemService(this.context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
//        }
//        try {
//            location = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//            return;
//        }
//        if (location == null) {
//            return;
//        }

    }

    @Override
    public void onResume() {
       super.onResume();
    }


    public void onClickFindButton(View view) {
        Intent launchFindIntent = new Intent(WelcomeSplashActivity.this, BathroomMapsActivity.class);
        launchFindIntent.putExtra(getString(R.string.maps_intent_latitude), location.getLatitude());
        launchFindIntent.putExtra(getString(R.string.maps_intent_longitude), location.getLongitude());
        WelcomeSplashActivity.this.startActivity(launchFindIntent);

    }

    public void onClickRateButton(View view) {
        Intent launchRateIntent = new Intent(WelcomeSplashActivity.this, AddActivity.class);
        WelcomeSplashActivity.this.startActivity(launchRateIntent);
    }
}
