package cs.tufts.edu.easy.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import cs.tufts.edu.easy.LocationHelper;
import cs.tufts.edu.easy.R;
import io.fabric.sdk.android.Fabric;


@SuppressWarnings("MissingPermission")
public class WelcomeSplashActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 270; // let Android studio set these
    private static final String TAG = WelcomeSplashActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    // TODO switch to fusedlocationapi
    private LocationManager locationManager;
    private Location currentLocation = null;


    private Button findButton;
    private Button reviewButton;
    private SignInButton signInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        getViews();
        findButton.setOnClickListener(this);
        reviewButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);


        // setup sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this) // activity, onFailedListener
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();


        // setup location listener
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (hasLocationPermission()) {
            Toast.makeText(this, "requesting location", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            requestLocationPermission();
        }
    }

    private void getViews() {
        findButton = (Button) findViewById(R.id.welcome_find_bathroom_button);
        reviewButton = (Button) findViewById(R.id.welcome_review_bathroom_button);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result from google authentication
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "Google authentication failed - try again", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    // Show sign in button if user is not signed in
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            signInButton.setVisibility(View.INVISIBLE);
            signInButton.setClickable(false);

            reviewButton.setVisibility(View.VISIBLE);
            reviewButton.setClickable(true);

            findButton.setVisibility(View.VISIBLE);
            reviewButton.setClickable(true);
        } else {
            reviewButton.setVisibility(View.INVISIBLE);
            reviewButton.setClickable(false);

            findButton.setVisibility(View.INVISIBLE);
            reviewButton.setClickable(false);

            signInButton.setVisibility(View.VISIBLE);
            signInButton.setClickable(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } else {
                    Toast.makeText(this, "EasyStream uses your currentLocation to \n help you find and add bathrooms.",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "We didn't find your location: trying again", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            return;
        }

        if (view == findViewById(R.id.welcome_find_bathroom_button)) {
            Intent launchFindIntent = new Intent(WelcomeSplashActivity.this, BathroomMapsActivity.class);
            launchFindIntent.putExtra(getString(R.string.maps_intent_latitude), currentLocation.getLatitude());
            launchFindIntent.putExtra(getString(R.string.maps_intent_longitude), currentLocation.getLongitude());
            WelcomeSplashActivity.this.startActivity(launchFindIntent);

        } else if (view == findViewById(R.id.welcome_review_bathroom_button)) {
            Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
            return;
//            Intent launchRateIntent = new Intent(WelcomeSplashActivity.this, AddBathroomActivity.class);
//            launchRateIntent.putExtra(getString(R.string.maps_intent_latitude), currentLocation.getLatitude());
//            launchRateIntent.putExtra(getString(R.string.maps_intent_longitude), currentLocation.getLongitude());
//            WelcomeSplashActivity.this.startActivity(launchRateIntent);

        } else if (view == findViewById(R.id.sign_in_button)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Can't connect to server...", Toast.LENGTH_SHORT).show();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(WelcomeSplashActivity.this, "Location changed to: " + location.toString(), Toast.LENGTH_SHORT).show();
            if (LocationHelper.isBetterLocation(location, currentLocation)) {
                Toast.makeText(WelcomeSplashActivity.this, "got better location from " + location.getProvider(), Toast.LENGTH_SHORT).show();
                currentLocation = location;
            } else {
                Toast.makeText(WelcomeSplashActivity.this, "got worse location from " + location.getProvider(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
