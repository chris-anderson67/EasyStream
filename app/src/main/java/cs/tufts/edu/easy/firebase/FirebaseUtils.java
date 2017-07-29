package cs.tufts.edu.easy.firebase;

import cs.tufts.edu.easy.constants.DatabaseKeys;


public class FirebaseUtils {
    public static boolean isMetaData(String s) {
        return s.equals(DatabaseKeys.CREATED_AT) || s.equals(DatabaseKeys.UPDATED_AT);
    }
}
