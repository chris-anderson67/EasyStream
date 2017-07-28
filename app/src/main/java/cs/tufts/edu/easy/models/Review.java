package cs.tufts.edu.easy.models;


import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    public  int id;
    public  int bathroom_id;
    public  double rating;
    public  double cleanliness;
    public  boolean active;
    public  String comment;
    public  String added_on;
    public  String added_by;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBathroom_id() {
        return bathroom_id;
    }

    public void setBathroom_id(int bathroom_id) {
        this.bathroom_id = bathroom_id;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(double cleanliness) {
        this.cleanliness = cleanliness;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAdded_on() {
        return added_on;
    }

    public void setAdded_on(String added_on) {
        this.added_on = added_on;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }
}
