package cs.tufts.edu.easy.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cs.tufts.edu.easy.models.Bathroom;

public abstract class BaseAddPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        setupViews(rootView);
        return rootView;
    }

    protected abstract void setupViews(View rootView);

    @Override
    public void onPause() {
        super.onPause();
    }

    public abstract int getLayoutResource();

    /**
     * @param initialBathroom
     * @return Updated bathroom, null if incomplete information
     */
    public abstract Bathroom getUpdatedBathroom(Bathroom initialBathroom);

}
