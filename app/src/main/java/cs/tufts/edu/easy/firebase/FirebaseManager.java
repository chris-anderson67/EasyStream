package cs.tufts.edu.easy.firebase;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import cs.tufts.edu.easy.Constants.DatabaseKeys;
import cs.tufts.edu.easy.models.Bathroom;


public class FirebaseManager {

    private static final String TAG = "FIREBASE_MANAGER";

    public static void addComment(String comment, String id) {
        Log.d("TAG", "Adding "+ comment);
        getCommentsReference().child(id).push().setValue(comment);
    }

    /**
     * Adds new bathroom to Firebase database, updates currentLocation information for geofire
     * MOST OF THIS WOULD IDEALLY BE DONE ON THE BACKEND
     *
     * @param newBathroom The bathroom to add to DB
     */
    public static synchronized boolean addBathroom(Context context, Bathroom newBathroom) {
        if (newBathroom == null || newBathroom.comment == null ||
                newBathroom.latitude == 0.0 || newBathroom.longitude == 0.0) {
            return false;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "addBathroom: User is null!", null);
            return false;
        }
        String userId = user.getUid();

        // Add the address
        Geocoder geoCoder = new Geocoder(context);
        try {
            List<Address> addresses = geoCoder.getFromLocation(newBathroom.latitude, newBathroom.longitude, 1);
            Address address = addresses.get(0);

            newBathroom.street = address.getSubThoroughfare() + " " + address.getThoroughfare();
            newBathroom.country = address.getCountryName();
            newBathroom.city = address.getLocality();
            Log.e("addBathroom", "addBathroom: " + newBathroom.toString(), null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Auto-generate key for new bathroom
        DatabaseReference newBathroomRef = getBathroomsReference();
        String newBathroomId = newBathroomRef.push().getKey();
        Log.e(TAG, "addBathroom: " + "got key", null);

        // Add the bathroom
        newBathroomRef.child(newBathroomId).setValue(newBathroom);
        Log.e(TAG, "addBathroom: " + "added bathroom", null);

        // Autogenerate key for comment
        DatabaseReference newCommentsRef = getCommentsReference().child(newBathroomId);
        String newCommentId = newCommentsRef.push().getKey();

        // Add the comment
        newCommentsRef.child(newCommentId).setValue(newBathroom.comment);
        Log.e(TAG, "addBathroom: " + "added comments", null);

        // Add currentLocation data to locations ref at new-bathroom's key
        GeoFire geoFire = new GeoFire(getLocationsReference());
        geoFire.setLocation(newBathroomId,
                new GeoLocation(newBathroom.latitude, newBathroom.longitude));
        Log.e(TAG, "addBathroom: " + "added locations", null);

        // Associate the bathroom with the user
        DatabaseReference newUserDataRef = getUserDataReference().child(userId);
        newUserDataRef.child(DatabaseKeys.USER_DATA_BATHROOMS).child(newBathroomId).setValue(1);

        // Associate the comment with the user
        newUserDataRef.child(DatabaseKeys.USER_DATA_COMMENTS).child(newBathroomId).child(newCommentId).setValue(newBathroom.comment);

        // Associate the vote with the user
        newUserDataRef.child(DatabaseKeys.USER_DATA_VOTES).child(newBathroomId).setValue(newBathroom.upvote - newBathroom.downvote);


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

//    public static DatabaseReference getCommentsTestReference() {
//        return FirebaseDatabase.getInstance().getReference("Test" + DatabaseKeys.COMMENTS_PATH);
//    }
//
//    public static DatabaseReference getBathroomsTestReference() {
//        return FirebaseDatabase.getInstance().getReference("Test" + DatabaseKeys.BATHROOMS_PATH);
//    }
//
//    public static DatabaseReference getLocationsTestReference() {
//        return FirebaseDatabase.getInstance().getReference("Test" + DatabaseKeys.LOCATIONS_PATH);
//    }
//
//    private static DatabaseReference getUserDataTestReference() {
//        return FirebaseDatabase.getInstance().getReference("Test" + DatabaseKeys.USER_DATA_PATH);
//    }

    public static boolean isMetaData(String s) {
        return s.equals(DatabaseKeys.CREATED_AT) || s.equals(DatabaseKeys.UPDATED_AT);
    }
}
