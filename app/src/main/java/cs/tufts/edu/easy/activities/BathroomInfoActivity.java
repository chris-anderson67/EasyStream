package cs.tufts.edu.easy.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cs.tufts.edu.easy.Constants;
import cs.tufts.edu.easy.LocationHelper;
import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.firebase.FirebaseManager;
import cs.tufts.edu.easy.models.Bathroom;

import static cs.tufts.edu.easy.R.id.bathroom_details_downvote_button;
import static cs.tufts.edu.easy.R.id.bathroom_details_map;

public class BathroomInfoActivity extends AppCompatActivity implements ValueEventListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static String TAG = BathroomInfoActivity.class.getSimpleName();
    private static final long UPVOTE = 1;
    private static final long DOWNVOTE = -1;
    private static final long NO_VOTE = 0;

    private DatabaseReference bathroomReference;
    private DatabaseReference commentsReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String bathroomId;
    private Bathroom bathroom;
    private List<String> comments;

    private long userVote = NO_VOTE;

    private TextView nameView;
    private TextView streetView;
    private TextView scoreView;
    private CheckBox accessibleCheckBox;
    private CheckBox changingTableCheckBox;
    private CheckBox unisexCheckBox;
    private LinearLayout commentsView;
    private FloatingActionButton addCommentButton;
    private ImageView upvoteButton;
    private ImageView downvoteButton;
    private ScrollView scrollView;

    private GoogleMap map;
    private SupportMapFragment locationMap;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_info);

        getViews();
        addListeners();
        comments = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        bathroomId = getIntent().getStringExtra(Constants.IntentKeys.BATHROOM_ID);
        bathroomReference = FirebaseDatabase.getInstance().getReference(getString(R.string.bathrooms_db_path)).child(bathroomId);
        commentsReference = FirebaseDatabase.getInstance().getReference(getString(R.string.comments_db_path)).child(bathroomId);
        userReference = FirebaseManager.getUserDataReference().child(currentUser.getUid());

        locationMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(bathroom_details_map);
        locationMap.getMapAsync(this);
        Log.e(TAG, bathroomId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bathroomReference.removeEventListener(this);
        commentsReference.removeEventListener(this);
        userReference.removeEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser == null) {
            finish();
        }
        bathroomReference.addValueEventListener(this);
        commentsReference.addValueEventListener(this);
        userReference.addValueEventListener(this);
    }

    private void getViews() {
        nameView = (TextView) findViewById(R.id.bathroom_details_name);
        streetView = (TextView) findViewById(R.id.bathroom_details_street);
        scoreView = (TextView) findViewById(R.id.bathroom_details_score_view);
        commentsView = (LinearLayout) findViewById(R.id.bathroom_details_comments_list);
        addCommentButton = (FloatingActionButton) findViewById(R.id.add_comment_fab);
        changingTableCheckBox = (CheckBox) findViewById(R.id.bathroom_details_changing_table_checkbox);
        accessibleCheckBox = (CheckBox) findViewById(R.id.bathroom_details_accessible_checkbox);
        unisexCheckBox = (CheckBox) findViewById(R.id.bathroom_details_unisex_checkbox);
        upvoteButton = (ImageView) findViewById(R.id.bathroom_details_upvote_button);
        downvoteButton = (ImageView) findViewById(bathroom_details_downvote_button);
        scrollView = (ScrollView) findViewById(R.id.bathroom_details_scroll_view);
    }

    private void addListeners() {
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCommentAlert().show();
            }
        });
        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userVote = (userVote == UPVOTE) ? NO_VOTE : UPVOTE;
                recordVote();
            }
        });
        downvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userVote = (userVote == DOWNVOTE) ? NO_VOTE : DOWNVOTE;
                recordVote();
            }
        });
    }

    private void recordVote() {
        String userId = currentUser.getUid();
        DatabaseReference ref = FirebaseManager.getUserDataReference().child(userId);
        ref.child(Constants.DatabaseKeys.USER_DATA_VOTES).child(bathroomId).setValue(userVote);
    }

    private AlertDialog.Builder getCommentAlert() {
        AlertDialog.Builder commentAlert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        commentAlert.setTitle(R.string.new_comment);
        commentAlert.setMessage(R.string.new_comment_prompt);
        commentAlert.setView(input);
        commentAlert.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FirebaseManager.addComment(input.getText().toString(), bathroomId);
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
        // Comments change
        if (dataSnapshot.getRef().toString().equals(commentsReference.toString())) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                String comment = child.getValue(String.class);
                if (!FirebaseManager.isMetaData(child.getKey()) && !comments.contains(comment)) {
                    comments.add(comment);
                }
            }
            populateComments();

        // Bathroom data change / received
        } else if (dataSnapshot.getRef().toString().equals(bathroomReference.toString())) {
            bathroom = dataSnapshot.getValue(Bathroom.class);
            populateBathroomData();
            populateMap();

        // User data changed
        } else if (dataSnapshot.getRef().toString().equals(userReference.toString())) {
            DataSnapshot vote = dataSnapshot.child(Constants.DatabaseKeys.USER_DATA_VOTES).child(bathroomId);
            if (vote.exists()) {
                updateUiWithVote((long) vote.getValue());
            }
        }
    }

    private void updateUiWithVote(long vote) {
        if (vote == UPVOTE) {
            tint(upvoteButton, R.color.colorPrimary);
            tint(downvoteButton, R.color.button_disabled);
            Toast.makeText(this, String.valueOf(vote), Toast.LENGTH_SHORT).show();
        } else if (vote == DOWNVOTE) {
            tint(downvoteButton, R.color.colorPrimary);
            tint(upvoteButton, R.color.button_disabled);
            Toast.makeText(this, String.valueOf(vote), Toast.LENGTH_SHORT).show();
        } else {
            tint(upvoteButton, R.color.button_disabled);
            tint(downvoteButton, R.color.button_disabled);
            Toast.makeText(this, String.valueOf(vote), Toast.LENGTH_SHORT).show();
        }
    }

    private void tint(ImageView view, @ColorRes int color) {
        DrawableCompat.setTint(view.getDrawable(), ContextCompat.getColor(this, color));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getDetails());
        databaseError.toException().printStackTrace();
    }

    private void populateMap() {
        if (map != null) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(bathroom.latitude, bathroom.longitude))
                    .icon(LocationHelper.getMarkerIcon(this, R.color.colorAccent)));
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(bathroom.latitude, bathroom.longitude), 16));

            // map steals focus - scroll back to top
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        }
    }

    private void populateComments() {
        for (String comment : comments) {
            TextView textView = new TextView(this);
            textView.setText(comment);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = (int) getResources().getDimension(R.dimen.md_margin_medium);
            lp.setMargins(margin, 0, margin, margin);

            textView.setLayoutParams(lp);
            commentsView.addView(textView);
        }
    }

    private void populateBathroomData() {
        nameView.setText(bathroom.name);
        streetView.setText(bathroom.street);

        scoreView.setText(Integer.toString(bathroom.upvote - bathroom.downvote));

        accessibleCheckBox.setChecked(bathroom.accessible);
        unisexCheckBox.setChecked(bathroom.unisex);
        changingTableCheckBox.setChecked(bathroom.changing_table);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
