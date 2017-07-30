package cs.tufts.edu.easy.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.fragments.ConfirmationAddPageFragment;
import cs.tufts.edu.easy.fragments.FeedbackAddPageFragment;
import cs.tufts.edu.easy.fragments.LogisticsAddPageFragment;
import cs.tufts.edu.easy.fragments.NameAddPageFragment;
import cs.tufts.edu.easy.views.NoSwipeViewpager;

public class AddBathroomActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int NUM_PAGES = 4;

    private NoSwipeViewpager viewPager;
    private Button nextButton;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_pager);

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
        if (view == nextButton) {
            // last page
            if (currentItem == NUM_PAGES - 1) {
                finish();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    }

    private class AddBathroomFlowAdapter extends FragmentStatePagerAdapter {
        public AddBathroomFlowAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case (0):
                    return new NameAddPageFragment();

                case (1):
                    return new FeedbackAddPageFragment();

                case (2):
                    return new LogisticsAddPageFragment();

                case (3):
                    return new ConfirmationAddPageFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}

