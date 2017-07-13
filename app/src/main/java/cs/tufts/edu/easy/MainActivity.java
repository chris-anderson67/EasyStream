package cs.tufts.edu.easy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

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

        this.mLocationManager = (LocationManager) this.context.getSystemService(this.context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }
        try {
            location = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
            return;
        }
        if (location == null) {
            return;
        }

    }

    @Override
    public void onResume() {
       super.onResume();
        testAccessDb();
    }

    private void testAccessDb() {
        Toast.makeText(context, "TESTING DB", Toast.LENGTH_SHORT).show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("************", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("***ERROR*******", "Failed to read value.", error.toException());
            }
        });

        myRef.setValue("Hello, World!");
        myRef.setValue("Hello, World!");
        myRef.setValue("Hello, World!");
        myRef.setValue("Hello, World!");
        myRef.setValue("Hello, World!");
    }

    public void onClickFindButton(View view) {
        Intent launchFindIntent = new Intent(MainActivity.this, MapsActivity.class);
        MainActivity.this.startActivity(launchFindIntent);

    }

    public void onClickRateButton(View view) {
        Intent launchRateIntent = new Intent(MainActivity.this, AddActivity.class);
        MainActivity.this.startActivity(launchRateIntent);
    }
}
