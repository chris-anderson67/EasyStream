package cs.tufts.edu.easy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.activities.AddBathroomActivity;
import cs.tufts.edu.easy.models.Bathroom;


public class ConfirmationAddPageFragment extends BaseAddPageFragment{

    TextView textView = null;
    AddBathroomActivity activity = null;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // visible hint defaults to true, set it to false to start
        setUserVisibleHint(false);

        activity = (AddBathroomActivity) getActivity();
    }

    /**
     * Called with isVisibleToUser == true when shown
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (textView != null) {
                textView.setText(activity.getBathroom().toString());
            }
        }
    }

    @Override
    protected void setupViews(View rootView) {
        textView = (TextView) rootView.findViewById(R.id.confirmation_text);
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
