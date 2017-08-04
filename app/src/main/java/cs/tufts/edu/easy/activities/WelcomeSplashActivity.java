package cs.tufts.edu.easy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import cs.tufts.edu.easy.R;
import io.fabric.sdk.android.Fabric;


public class WelcomeSplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 270; // let Android studio set these
    private FusedLocationProviderClient mFusedLocationClient;
    public Location currentLocation = null;

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "EasyStream uses your currentLocation to \n help you find and add bathrooms.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @SuppressWarnings("MissingPermission") // handle elsewhere
    private void getCurrentLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                        }
                    }
                });
    }

    private void getViews() {
        findButton = (Button) findViewById(R.id.welcome_find_bathroom_button);
        reviewButton = (Button) findViewById(R.id.welcome_review_bathroom_button);
    }

    @Override
    public void onClick(View view) {
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "We can't find you. Trying again...", Toast.LENGTH_SHORT).show();
            getCurrentLocation();
            return;
        }

        if (view == findViewById(R.id.welcome_find_bathroom_button)) {
            Intent launchFindIntent = new Intent(WelcomeSplashActivity.this, BathroomMapsActivity.class);
            launchFindIntent.putExtra(getString(R.string.maps_intent_latitude), currentLocation.getLatitude());
            launchFindIntent.putExtra(getString(R.string.maps_intent_longitude), currentLocation.getLongitude());
            WelcomeSplashActivity.this.startActivity(launchFindIntent);

        } else if (view == findViewById(R.id.welcome_review_bathroom_button)) {
            Intent launchRateIntent = new Intent(WelcomeSplashActivity.this, AddBathroomActivity.class);
            launchRateIntent.putExtra(getString(R.string.maps_intent_latitude), currentLocation.getLatitude());
            launchRateIntent.putExtra(getString(R.string.maps_intent_longitude), currentLocation.getLongitude());
            WelcomeSplashActivity.this.startActivity(launchRateIntent);
        }
    }

    private boolean hasLocationPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);

    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_ACCESS_FINE_LOCATION);
    }
}
