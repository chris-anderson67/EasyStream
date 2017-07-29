package cs.tufts.edu.easy.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.constants.IntentKeys;
import cs.tufts.edu.easy.firebase.FirebaseUtils;
import cs.tufts.edu.easy.models.Bathroom;

public class BathroomInfoActivity extends AppCompatActivity {

    private DatabaseReference bathroomReference;
    private DatabaseReference commentsReference;

    private String bathroomId;
    private Bathroom bathroom;
    private List<String> comments;
    private ArrayAdapter<String> commentsAdapter;

    private TextView nameView;
    private TextView streetView;
    private TextView cityCountryView;
    private TextView scoreView;
    private ListView commentsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_info);

        getViews();
        comments = new ArrayList<>();

        bathroomId = getIntent().getStringExtra(IntentKeys.BATHROOM_ID);
        bathroomReference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.bathrooms_db_path)).child(bathroomId);
        commentsReference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.comments_db_path)).child(bathroomId);

        addSingleValueListeners();
    }

    private void addSingleValueListeners() {
        bathroomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bathroom = dataSnapshot.getValue(Bathroom.class);
                populateBathroomData(bathroom);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        commentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String comment = child.getValue(String.class);
                    if (!FirebaseUtils.isMetaData(child.getKey())) {
                        comments.add(comment);
                    }
                }
                for (String comment : comments) {
                    Toast.makeText(BathroomInfoActivity.this, comment, Toast.LENGTH_SHORT).show();
                }
                commentsAdapter = new ArrayAdapter<String>(BathroomInfoActivity.this, android.R.layout.simple_list_item_1, comments);
                commentsView.setAdapter(commentsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateBathroomData(Bathroom bathroom) {
        nameView.setText(bathroom.name);
        streetView.setText(bathroom.street);
        cityCountryView.setText(bathroom.city + ", " + bathroom.country);
        scoreView.setText(Integer.toString(bathroom.upvote - bathroom.downvote) +
                          " (+" + Integer.toString(bathroom.upvote) + ", -" + Integer.toString(bathroom.downvote) + ")");
    }

    private void getViews() {
        nameView = (TextView) findViewById(R.id.bathroom_details_name);
        streetView = (TextView) findViewById(R.id.bathroom_details_street);
        cityCountryView = (TextView) findViewById(R.id.bathroom_details_city_country);
        scoreView = (TextView) findViewById(R.id.bathroom_details_score);
        commentsView = (ListView) findViewById(R.id.bathroom_details_comments_list);
    }
}
