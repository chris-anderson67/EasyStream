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

    @Override
    public String toString() {
        return "ID: " + String.valueOf(id) + "\n" +
                "Accessible: " + String.valueOf(accessible) + "\n" +
                "Changing Table: " + String.valueOf(changing_table) + "\n" +
                "Unisex: " + String.valueOf(unisex) + "\n" +
                "DownVotes: " + String.valueOf(downvote) + "\n"  +
                "UpVotes: " + String.valueOf(upvote) + "\n" +
                "Name: " + name + "\n" +
                "Country: " + country + "\n" +
                "City: " + city + "\n" +
                "Street: " + street + "\n" +
                "Comment: " + comment + "\n" +
                "Directions: " + directions + "\n" +
                "Latitude: " + String.valueOf(latitude) + "\n" +
                "Longitude: " + String.valueOf(latitude) + "\n" +
                "Created At: " + created_at + "\n" +
                "Updated At: " + updated_at + "\n";
    }

}
