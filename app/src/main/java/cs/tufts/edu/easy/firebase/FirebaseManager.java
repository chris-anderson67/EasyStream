package cs.tufts.edu.easy.firebase;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cs.tufts.edu.easy.Constants.DatabaseKeys;
import cs.tufts.edu.easy.models.Bathroom;


public class FirebaseManager {

    public static void addComment(String comment, String id) {
        Log.d("TAG", "Adding "+ comment);
        getCommentsReference().child(id).push().setValue(comment);
    }

    /**
     * Adds new bathroom to Firebase database, updates currentLocation information for geofire
     * @param newBathroom The bathroom to add to DB
     */
    public static boolean addBathroom(Bathroom newBathroom) {
        if (newBathroom == null || newBathroom.comment == null ||
                newBathroom.latitude == 0.0 || newBathroom.longitude == 0.0) {
            return false;
        }

        // Auto-generate key for new bathroom, add bathroom
        DatabaseReference newBathroomRef = getBathroomsTestReference();
        newBathroomRef.setValue(newBathroom);

        // Add list of comments at the new-bathroom's key in comments reference, add fist comment
        DatabaseReference newCommentsRef = getCommentsTestReference().child(newBathroomRef.getKey());
        newCommentsRef.push().setValue(newBathroom.comment);

        // Add currentLocation data to locations ref at new-bathroom's key
        GeoFire geoFire = new GeoFire(getLocationsTestReference());
        geoFire.setLocation(newBathroomRef.getKey(),
                new GeoLocation(newBathroom.latitude, newBathroom.longitude));

        return true;
    }

    public static DatabaseReference getCommentsReference() {
        return FirebaseDatabase.getInstance().getReference(DatabaseKeys.COMMENTS_PATH);
    }

    public static DatabaseReference getBathroomsReference() {
        return FirebaseDatabase.getInstance().getReference(DatabaseKeys.BATHROOMS_PATH);
    }

    public static DatabaseReference getLocationsReference() {
        return FirebaseDatabase.getInstance().getReference(DatabaseKeys.LOCATIONS_PATH);
    }

    public static DatabaseReference getUserDataReference() {
        return FirebaseDatabase.getInstance().getReference(DatabaseKeys.USER_DATA_PATH);
    }

    public static DatabaseReference getCommentsTestReference() {
        return FirebaseDatabase.getInstance().getReference("Test" + DatabaseKeys.COMMENTS_PATH);
    }

    public static DatabaseReference getBathroomsTestReference() {
        return FirebaseDatabase.getInstance().getReference("Test" + DatabaseKeys.BATHROOMS_PATH);
    }

    public static DatabaseReference getLocationsTestReference() {
        return FirebaseDatabase.getInstance().getReference("Test" + DatabaseKeys.LOCATIONS_PATH);
    }

    public static boolean isMetaData(String s) {
        return s.equals(DatabaseKeys.CREATED_AT) || s.equals(DatabaseKeys.UPDATED_AT);
    }
}
