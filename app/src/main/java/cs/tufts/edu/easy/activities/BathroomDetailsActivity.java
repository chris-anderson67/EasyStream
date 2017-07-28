package cs.tufts.edu.easy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


// TODO - cleanup this fucking activity
public class BathroomDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bathroom__details);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setTitle("Bathroom Details");
//        Intent mIntent = getIntent();
//        int tag = mIntent.getIntExtra(getString(R.string.bathroom_details_activity_intent_key), 0);
//        Log.v("BATHROOMDETAILS-ID", String.valueOf(tag));
//        String s = "";
//        JSONArray jArray = null;
//
//        //boolean value determines whether you're requesting all bathrooms data
//        try {
//            s = new GetData(false).execute().get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        try {
//            jArray = new JSONArray(s);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        /* Get Basic Bathroom Information */
//        HashMap<Integer, Bathroom> bathrooms = BathroomMapsActivity.bathroomMap;
//        TextView id_text = (TextView) findViewById(R.id.bathroom_id);
//        TextView title_text = (TextView) findViewById(R.id.bathroom_title);
//        Bathroom bathroom = bathrooms.get(tag);
//        String title = bathroom.bathroom_name;
//        title_text.setText(title);
//        if (bathroom.isLocked()) {
//            CheckBox lockedBox = (CheckBox)findViewById(R.id.detailsLockedBox);
//            lockedBox.setChecked(true);
//        }
//        if (bathroom.isBaby_station()) {
//            CheckBox babyBox = (CheckBox)findViewById(R.id.detailsBabyBox);
//            babyBox.setChecked(true);
//        }
//        if (bathroom.isCustomers_only()) {
//            CheckBox customersBox = (CheckBox)findViewById(R.id.detailsCustomersBox);
//            customersBox.setChecked(true);
//        }
//
//        double avgRating = bathrooms.get(tag).rating;
//
//        /* Populate Comments List */
//        ArrayList<Double> ratings = new ArrayList<>();
//        for (int i = 0; i < jArray.length(); i++) {
//            try {
//                JSONObject oneObject = jArray.getJSONObject(i);
//                Log.v("CURRENT_BATHROOM_ID", String.valueOf(oneObject.getInt("bathroom_id")));
//                if (oneObject.getInt("bathroom_id") == tag) {
//                    Log.v("ID's MATCH", String.valueOf(oneObject.getInt("bathroom_id")));
//                    Log.v("COMMENTS:", oneObject.getString("comments"));
//                    id_text.append(oneObject.getString("added_by") + ":\n");
//                    id_text.append(oneObject.getString("comments") + "\n\n");
//                    ratings.add(oneObject.getDouble("rating"));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        if (id_text.getText() == "") {
//            id_text.setText("No Comments Yet!");
//        }
//        if (avgRating <= 0) {
//            double sum = 0;
//            for (int i = 0; i < ratings.size(); i++) {
//                Log.d("RATING:", String.valueOf(ratings.get(i)));
//                sum += ratings.get(i);
//            }
//            avgRating = sum / (double) ratings.size();
//        }
//        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingStars);
//        ratingBar.setRating((float) avgRating);
    }
}
