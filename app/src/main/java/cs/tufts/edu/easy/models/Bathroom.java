package cs.tufts.edu.easy.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Bathroom {

    public int id = -1;

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
    public String directions;
    public double latitude = 0.0;
    public double longitude = 0.0;

    public String created_at;
    public String updated_at;

    public Bathroom() {

    }

}
