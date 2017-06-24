package cs.tufts.edu.easy;


import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    // TODO: Change to private, add getters, setters
    public int id;
    public int bathroom_id;
    public double rating;
    public double cleanliness;
    public boolean active;
    public String comment;
    public String added_on;
    public String added_by;

    // TODO: Allow partial population of review object
    public Review(JSONObject jObject) {
        try {
            this.id = jObject.getInt("id");
            this.bathroom_id = jObject.getInt("bathroom_id");
            this.rating = jObject.getDouble("rating");
            this.cleanliness =jObject.getDouble("cleanliness");
            this.active = jObject.getBoolean("active");
            this.comment = jObject.getString("comments");
            this.added_on = jObject.getString("added_on");
            this.added_by = jObject.getString("added_by");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
