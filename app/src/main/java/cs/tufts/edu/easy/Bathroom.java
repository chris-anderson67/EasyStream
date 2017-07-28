package cs.tufts.edu.easy;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Bathroom {

    public int id;

    public boolean accessible;
    public boolean changing_table;
    public boolean unisex;
    public int downvote;
    public int upvote;

    public String name;
    public String country;
    public String city;
    public String street;
    public String comment;
    public double latitude;
    public double longitude;

    public String created_at;
    public String updated_at;
    public String directions;

    public Bathroom() {

    }

}
