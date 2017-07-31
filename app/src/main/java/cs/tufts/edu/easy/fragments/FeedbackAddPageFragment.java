package cs.tufts.edu.easy.fragments;


import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.models.Bathroom;

public class FeedbackAddPageFragment extends BaseAddPageFragment implements View.OnClickListener{
    private EditText commentsField;
    private Button upvoteButton;
    private Button downvoteButton;

    private boolean upvoted;
    private boolean voted = false;

    @Override
    protected void setupViews(View rootView) {
        commentsField = (EditText) rootView.findViewById(R.id.feedback_fragment_comments);
        upvoteButton = (Button) rootView.findViewById(R.id.feedback_fragment_upvote_button);
        downvoteButton = (Button) rootView.findViewById(R.id.feedback_fragment_downvote_button);

        downvoteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_disabled));
        upvoteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_disabled));

        upvoteButton.setOnClickListener(this);
        downvoteButton.setOnClickListener(this);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feedback_add_fragment_page;
    }

    @Override
    public Bathroom getUpdatedBathroom(Bathroom initialBathroom) {
        // TODO test this
        if (!voted || commentsField.getText().toString().equals("")) {
            return null;
        }

        initialBathroom.upvote += upvoted ? 1 : 0;
        initialBathroom.downvote += upvoted ? 0 : 1;
        initialBathroom.comment = commentsField.getText().toString();
        return initialBathroom;
    }

    @Override
    public void onClick(View v) {
        if (v == upvoteButton) {
            upvoteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            downvoteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_disabled));
            upvoted = true;
        } else if (v == downvoteButton) {
            downvoteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            upvoteButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_disabled));
            upvoted = false;
        }
        voted = true;
    }
}
