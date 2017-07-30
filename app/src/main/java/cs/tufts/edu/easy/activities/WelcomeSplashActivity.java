package cs.tufts.edu.easy.activities;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;

import cs.tufts.edu.easy.R;
import io.fabric.sdk.android.Fabric;


public class WelcomeSplashActivity extends AppCompatActivity implements View.OnClickListener{

    private int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 0;
    private LocationManager mLocationManager;
    public static Location location;

    private Button findButton;
    private Button reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        getViews();
        findButton.setOnClickListener(this);
        reviewButton.setOnClickListener(this);

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

    private void getViews() {
        findButton = (Button) findViewById(R.id.welcome_find_bathroom_button);
        reviewButton = (Button) findViewById(R.id.welcome_review_bathroom_button);
    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.welcome_find_bathroom_button)) {
            Intent launchFindIntent = new Intent(WelcomeSplashActivity.this, BathroomMapsActivity.class);
            launchFindIntent.putExtra(getString(R.string.maps_intent_latitude), location.getLatitude());
            launchFindIntent.putExtra(getString(R.string.maps_intent_longitude), location.getLongitude());
            WelcomeSplashActivity.this.startActivity(launchFindIntent);

        } else if (view == findViewById(R.id.welcome_review_bathroom_button)) {
            Intent launchRateIntent = new Intent(WelcomeSplashActivity.this, AddBathroomActivity.class);
            WelcomeSplashActivity.this.startActivity(launchRateIntent);
        }
    }
}
