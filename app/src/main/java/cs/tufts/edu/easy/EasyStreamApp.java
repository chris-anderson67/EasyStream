package cs.tufts.edu.easy;

import com.google.firebase.database.FirebaseDatabase;

public class EasyStreamApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
