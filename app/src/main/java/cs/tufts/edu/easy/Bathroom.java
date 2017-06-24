package cs.tufts.edu.easy;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class Bathroom {

    // TODO: Change to private, add getters, setters
    public int id;
    public String bathroom_name;
    public String address;
    public double latitude;
    public double longitude;
    public boolean active;
    public String added_on;
    public String added_by;
    public String updated_on;
    public String updated_by;

    public String comments;
    public boolean locked;
    public boolean baby_station;
    public boolean customers_only;
    public double rating; // out of 5 stars
    public int rating_count;
    public double rating_total;
    public List<Review> reviews = null;


    // TODO - allow partial population of Bathroom object
    public Bathroom(JSONObject jObject) {
        this.rating_total = 0;
        this.rating_count = 0;
        try {
            this.id = jObject.getInt("id");
            this.bathroom_name = jObject.getString("bathroom_name");
            this.address = jObject.getString("address");
            this.latitude = jObject.getDouble("latitude");
            this.longitude = jObject.getDouble("longitude");
            this.active = Boolean.parseBoolean(jObject.getString("active"));
            this.added_on = jObject.getString("added_on");
            this.added_by = jObject.getString("added_by");
            this.updated_on = jObject.getString("updated_on");
            this.updated_by = jObject.getString("updated_by");
            this.comments = jObject.getString("comments");
            this.locked = Boolean.parseBoolean(jObject.getString("locked"));
            this.baby_station = Boolean.parseBoolean(jObject.getString("baby_station"));
            this.customers_only = Boolean.parseBoolean(jObject.getString("customers_only"));
            this.rating = jObject.getDouble("rating_avg");
            Log.v("GOT_RATING", String.valueOf(this.rating));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addReview(Review review) {
        this.rating_total = this.rating_total + review.rating;
        this.rating_count += 1;
        this.rating = this.rating_total / rating_count;
        this.updated_by = review.added_by;
        this.updated_on = review.added_on;

        this.reviews.add(review);
    }
}
