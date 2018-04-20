package cs.tufts.edu.easy.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cs.tufts.edu.easy.AddBathroomFlowAdapter;
import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.fragments.BaseAddPageFragment;
import cs.tufts.edu.easy.models.Bathroom;
import cs.tufts.edu.easy.views.NoSwipeViewpager;

public class AddBathroomActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int NUM_STEPS = 4;
    private static final String TAG = AddBathroomActivity.class.getSimpleName();

    private NoSwipeViewpager viewPager;
    private Button nextButton;
    private AddBathroomFlowAdapter pagerAdapter;

    private Bathroom bathroom = new Bathroom();
    private Location currentLocation = new Location("");
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_pager);

        Intent intent = getIntent();
        currentLocation.setLatitude(intent.getDoubleExtra(getString(R.string.maps_intent_latitude), 0.0));
        currentLocation.setLongitude(intent.getDoubleExtra(getString(R.string.maps_intent_longitude), 0.0));

        // Only allow authenticated users to proceed
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
            finish();
        }


        getViews();
        pagerAdapter = new AddBathroomFlowAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        nextButton.setOnClickListener(this);
    }

    private void getViews() {
        viewPager = (NoSwipeViewpager) findViewById(R.id.add_activity_view_pager);
        nextButton = (Button) findViewById(R.id.add_activity_next_button);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onClick(View view) {
        int currentItem = viewPager.getCurrentItem();
        BaseAddPageFragment pageFragment = (BaseAddPageFragment) pagerAdapter.getCurrentFragment();
        Bathroom updatedBathroom = pageFragment.getUpdatedBathroom(bathroom);


        if (updatedBathroom == null) {
            Toast.makeText(this, "Please fill out all information", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d(TAG, updatedBathroom.toString());
            bathroom = updatedBathroom;
        }

        if (view == nextButton) {
            if (currentItem == NUM_STEPS - 1) {
                finish();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    }

    public void updateNewBathroom(Bathroom newBathroom) {
        this.bathroom = newBathroom;
    }

    public Bathroom getBathroom() {
        return bathroom;
    }

    @Nullable
    public Location getCurrentLocation() {
        return currentLocation;
    }

}

