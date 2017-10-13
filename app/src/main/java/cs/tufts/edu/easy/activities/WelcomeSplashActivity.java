package cs.tufts.edu.easy.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import cs.tufts.edu.easy.Constants;
import cs.tufts.edu.easy.R;
import io.fabric.sdk.android.Fabric;

import static cs.tufts.edu.easy.Constants.PermissionCodes;


public class WelcomeSplashActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = WelcomeSplashActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

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

        if (!hasLocationPermission()) {
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
            case (PermissionCodes.ACCESS_FINE_LOCATION): {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thanks!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "EasyStream uses your currentLocation to \n help you find and add bathrooms.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.sign_in_button)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else if (!hasLocationPermission()) {
            requestLocationPermission();
        } else if (view == findViewById(R.id.welcome_find_bathroom_button)) {
            Intent i = new Intent(WelcomeSplashActivity.this, BathroomMapActivity.class);
            WelcomeSplashActivity.this.startActivity(i);
        } else if (view == findViewById(R.id.welcome_review_bathroom_button)) {
            Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
//            Intent launchRateIntent = new Intent(WelcomeSplashActivity.this, AddBathroomActivity.class);
//            launchRateIntent.putExtra(getString(R.string.maps_intent_latitude), currentLocation.getLatitude());
//            launchRateIntent.putExtra(getString(R.string.maps_intent_longitude), currentLocation.getLongitude());
//            WelcomeSplashActivity.this.startActivity(launchRateIntent);
        }

    }

    private boolean hasLocationPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.PermissionCodes.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Can't connect to server...", Toast.LENGTH_SHORT).show();
    }
}
