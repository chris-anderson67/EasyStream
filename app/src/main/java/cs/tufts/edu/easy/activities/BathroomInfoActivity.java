package cs.tufts.edu.easy.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import cs.tufts.edu.easy.firebase.FirebaseManager;
import cs.tufts.edu.easy.models.Bathroom;

public class BathroomInfoActivity extends AppCompatActivity implements ValueEventListener{

    public static String TAG = BathroomInfoActivity.class.getSimpleName();

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
    private Button addCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_info);

        getViews();
        comments = new ArrayList<>();

        bathroomId = getIntent().getStringExtra(IntentKeys.BATHROOM_ID);
        bathroomReference = FirebaseDatabase.getInstance().getReference(getString(R.string.bathrooms_db_path)).child(bathroomId);
        commentsReference = FirebaseDatabase.getInstance().getReference(getString(R.string.comments_db_path)).child(bathroomId);

        setupAddComment();
    }

    @Override
    protected void onPause() {
        super.onPause();

        bathroomReference.removeEventListener(this);
        commentsReference.removeEventListener(this);
    }

    @Override
    protected void onResume() {
       super.onResume();

        bathroomReference.addValueEventListener(this);
        commentsReference.addValueEventListener(this);
    }

    private void getViews() {
        nameView = (TextView) findViewById(R.id.bathroom_details_name);
        streetView = (TextView) findViewById(R.id.bathroom_details_street);
        cityCountryView = (TextView) findViewById(R.id.bathroom_details_city_country);
        scoreView = (TextView) findViewById(R.id.bathroom_details_score);
        commentsView = (ListView) findViewById(R.id.bathroom_details_comments_list);
        addCommentButton = (Button) findViewById(R.id.bathroom_details_add_comment_button);
    }

    private void setupAddComment() {
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommentAlert().show();
            }
        });
    }

    private AlertDialog.Builder getCommentAlert() {
        AlertDialog.Builder commentAlert = new AlertDialog.Builder(this);
        commentAlert.setTitle(R.string.new_comment);
        commentAlert.setMessage(R.string.new_comment_prompt);

        final EditText input = new EditText(this);
        commentAlert.setView(input);

        commentAlert.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FirebaseManager.addComment(input.getText().toString(), commentsReference);
            }
        });

        commentAlert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(BathroomInfoActivity.this, R.string.cancel_comment_confirmation, Toast.LENGTH_SHORT).show();
            }
        });

        return commentAlert;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getRef().toString().equals(commentsReference.toString())) {

            for (DataSnapshot child : dataSnapshot.getChildren()) {
                String comment = child.getValue(String.class);
                if (!FirebaseManager.isMetaData(child.getKey()) && !comments.contains(comment)) {
                    comments.add(comment);
                }
            }
            populateComments();

        } else if (dataSnapshot.getRef().toString().equals(bathroomReference.toString())) {
            bathroom = dataSnapshot.getValue(Bathroom.class);
            populateBathroomData(bathroom);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getDetails());
        databaseError.toException().printStackTrace();
    }

    private void populateComments() {
        commentsAdapter = new ArrayAdapter<String>(BathroomInfoActivity.this,
                android.R.layout.simple_list_item_1, comments);
        commentsView.setAdapter(commentsAdapter);
    }

    private void populateBathroomData(Bathroom bathroom) {
        nameView.setText(bathroom.name);
        streetView.setText(bathroom.street);
        cityCountryView.setText(bathroom.city + ", " + bathroom.country);
        scoreView.setText(Integer.toString(bathroom.upvote - bathroom.downvote) +
                " (+" + Integer.toString(bathroom.upvote) + ", -" + Integer.toString(bathroom.downvote) + ")");
    }


}
