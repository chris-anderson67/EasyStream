package cs.tufts.edu.easy.fragments;

import android.view.View;
import android.widget.EditText;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.models.Bathroom;


public class NameAddPageFragment extends BaseAddPageFragment {

    private EditText nameField;

    @Override
    protected void setupViews(View rootView) {
        nameField = (EditText) rootView.findViewById(R.id.name_add_fragment_edit_text);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.name_add_fragment_page;
    }

    @Override
    public Bathroom getUpdatedBathroom(Bathroom initialBathroom) {
        initialBathroom.name = nameField.getText().toString().trim();
        return initialBathroom.name.equals("") ? null : initialBathroom;
    }
}
