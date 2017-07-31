package cs.tufts.edu.easy;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import cs.tufts.edu.easy.activities.AddBathroomActivity;
import cs.tufts.edu.easy.fragments.ConfirmationAddPageFragment;
import cs.tufts.edu.easy.fragments.FeedbackAddPageFragment;
import cs.tufts.edu.easy.fragments.LogisticsAddPageFragment;
import cs.tufts.edu.easy.fragments.NameAddPageFragment;

public class AddBathroomFlowAdapter extends FragmentStatePagerAdapter {
    private Fragment currentFragment;

    public AddBathroomFlowAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
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
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return AddBathroomActivity.NUM_STEPS;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}

