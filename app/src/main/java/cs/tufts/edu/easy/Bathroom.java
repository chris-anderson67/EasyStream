package cs.tufts.edu.easy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by christopheranderson on 10/23/16.
 */

public class Bathroom {

    // TODO: Change to private, add getters, setters
    public int id;
    public String bathroom_name;
    public String address;
    public String latitude;
    public String longitude;
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


    // TODO: add rating_total and rating_count to DB
    public Bathroom(JSONObject jObject) {
        this.rating_total = 0;
        this.rating_count = 0;
        try {
            this.id = jObject.getInt("id");
            this.bathroom_name = jObject.getString("bathroom_name");
            this.address = jObject.getString("address");
            this.latitude = jObject.getString("latitude");
            this.longitude = jObject.getString("longitude");
            this.active = jObject.getBoolean("active");
            this.added_on = jObject.getString("added_on");
            this.added_by = jObject.getString("added_by");
            this.updated_on = jObject.getString("updated_on");
            this.updated_by = jObject.getString("updated_by");
            this.comments = jObject.getString("comments");
            this.locked = jObject.getBoolean("locked");
            this.baby_station = jObject.getBoolean("baby_station");
            this.customers_only = jObject.getBoolean("customers_only");
            this.rating = jObject.getDouble("rating_avg");
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
