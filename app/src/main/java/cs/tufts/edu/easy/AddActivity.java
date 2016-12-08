package cs.tufts.edu.easy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.Barcode.Address;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    double latitude = 0;
    double longitude = 0;
    public static Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Bathroom");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        location = MainActivity.location;
        final EditText latBox = (EditText) findViewById(R.id.latBox);
        final EditText lngBox = (EditText) findViewById(R.id.lngBox);
        if (location != null) {
            String lat = String.valueOf(location.getLatitude());
            String lng = String.valueOf(location.getLongitude());
            latBox.setText(lat);
            lngBox.setText(lng);
        }
    }

    public void onSubmitClick(View view) {
        final CheckBox babyCheck = (CheckBox) findViewById(R.id.baby_check);
        final CheckBox lockedBox = (CheckBox) findViewById(R.id.lockedBox);
        final CheckBox customersBox = (CheckBox) findViewById(R.id.customers_check);
        final EditText nameText = (EditText) findViewById(R.id.name);
        final EditText usernameText = (EditText) findViewById(R.id.username);
        final EditText commentText = (EditText) findViewById(R.id.comment);
        final EditText addressBox = (EditText) findViewById(R.id.addressBox);
        final EditText latBox = (EditText) findViewById(R.id.latBox);
        final EditText lngBox = (EditText) findViewById(R.id.lngBox);
        final RatingBar cBar = (RatingBar) findViewById(R.id.cleanlinessBar);
        final RatingBar rBar = (RatingBar) findViewById(R.id.ratingBar);

        String address = addressBox.getText().toString();
        String lat = latBox.getText().toString();
        String lng = lngBox.getText().toString();
        String name = nameText.getText().toString();
        String comment = commentText.getText().toString();
        String username = usernameText.getText().toString();
        String baby = "0";
        String locked = "0";
        String customers_only = "0";

        float cleanlinessF = cBar.getRating();
        String cleanliness = Float.toString(cleanlinessF);

        float ratingF = rBar.getRating();
        String rating = Float.toString(ratingF);

        // TODO: Get it to not submit with missing data
        if ((ratingF == 0) || (cleanlinessF == 0) || (address == "") || (lat == "") ||
                (lng == "") || (name == "") || (comment == "") || (username == "") ||
                (address == null) || (lat == null) || (lng == null) || (name == null) ||
                (comment == null) || (username == null)) {
            Toast.makeText(this, "Please fill out every field.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (lockedBox.isChecked()) {
            locked = "1";
        }
        if (babyCheck.isChecked()) {
            baby = "1";
        }
        if (customersBox.isChecked()) {
            customers_only = "1";
        }

        //boolean value determines whether you're requesting all bathrooms data
        AsyncTask<String, Integer, HttpResponse> task = new PostData().execute(name, username, comment, cleanliness, baby, rating, locked, customers_only, lat, lng, address);
    }
}
