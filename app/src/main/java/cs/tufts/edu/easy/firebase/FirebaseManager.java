package cs.tufts.edu.easy.firebase;

import com.google.firebase.database.DatabaseReference;

import cs.tufts.edu.easy.constants.DatabaseKeys;


public class FirebaseManager {

    public static boolean isMetaData(String s) {
        return s.equals(DatabaseKeys.CREATED_AT) || s.equals(DatabaseKeys.UPDATED_AT);
    }

    public static void addComment(String comment, DatabaseReference commentReference) {
        addChild(comment, commentReference);
    }

    public static void addChild(Object child, DatabaseReference ref) {
        ref.push().setValue(child);
    }
}
