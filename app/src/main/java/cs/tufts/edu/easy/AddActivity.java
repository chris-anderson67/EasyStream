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

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.Barcode.Address;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    double latitude = 0;
    double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Bathroom");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void onSubmitClick(View view) {
        final EditText nameText = (EditText) findViewById(R.id.name);
        String name = nameText.getText().toString();
        final EditText usernameText = (EditText) findViewById(R.id.username);
        String username = usernameText.getText().toString();
        final EditText commentText = (EditText) findViewById(R.id.comment);
        String comment = commentText.getText().toString();
        final EditText addressBox = (EditText) findViewById(R.id.addressBox);
        final EditText latBox = (EditText) findViewById(R.id.latBox);
        final EditText lngBox = (EditText) findViewById(R.id.lngBox);
        String address = addressBox.getText().toString();
        String lat = latBox.getText().toString();
        String lng = lngBox.getText().toString();


//        final CheckBox maleCheck = (CheckBox) findViewById(R.id.male_check);
        final CheckBox babyCheck = (CheckBox) findViewById(R.id.baby_check);
        final CheckBox lockedBox = (CheckBox) findViewById(R.id.lockedBox);
        final CheckBox customersBox = (CheckBox) findViewById(R.id.customers_check);

//        final CheckBox femaleCheck = (CheckBox) findViewById(R.id.female_check);
//        final CheckBox neutralCheck = (CheckBox) findViewById(R.id.neutral_check);
//        String gender = "unknown";
        String baby = "0";
        String locked = "0";
        String customers_only = "0";
//        if (maleCheck.isChecked() && (femaleCheck.isChecked())) {
//            gender = "neutral";
//        }
//        else if (maleCheck.isChecked()) {
//            gender = "male";
//        }
//        else if (femaleCheck.isChecked()){
//            gender = "female";
//        }
//        else if (neutralCheck.isChecked()) {
//            gender = "neutral";
//        }
        if (lockedBox.isChecked()) {
            locked = "1";
        }
        if (babyCheck.isChecked()) {
            baby = "1";
        }
        if (customersBox.isChecked()) {
            customers_only = "1";
        }
        Log.v("LATITUDE", String.valueOf(latitude));
        Log.v("LONGITUDE", String.valueOf(longitude));

        RatingBar cBar = (RatingBar) findViewById(R.id.cleanlinessBar);
        float cleanlinessF = cBar.getRating();
        String cleanliness = Float.toString(cleanlinessF);

        RatingBar rBar = (RatingBar) findViewById(R.id.ratingBar);
        float ratingF = rBar.getRating();
        String rating = Float.toString(ratingF);

        //boolean value determines whether you're requesting all bathrooms data
        AsyncTask<String, Integer, HttpResponse> task = new PostData().execute(name, username, comment, cleanliness, baby, rating, locked, customers_only, lat, lng, address);

    }
}
