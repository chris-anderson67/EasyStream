package cs.tufts.edu.easy.fragments;

import android.view.View;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.models.Bathroom;


public class ConfirmationAddPageFragment extends BaseAddPageFragment{

    @Override
    protected void setupViews(View rootView) {

    }

    @Override
    public int getLayoutResource() {
        return R.layout.confirmation_add_fragment_page;
    }

    @Override
    public Bathroom getUpdatedBathroom(Bathroom initialBathroom) {
        return initialBathroom;
    }

}
