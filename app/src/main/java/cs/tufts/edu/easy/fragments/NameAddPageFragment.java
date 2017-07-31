package cs.tufts.edu.easy.fragments;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.models.Bathroom;


public class NameAddPageFragment extends BaseAddPageFragment {

    private EditText nameField;
    private String name;

    @Override
    protected void setupViews(View rootView) {
        nameField = (EditText) rootView.findViewById(R.id.name_add_fragment_edit_text);
        addTextListener();
    }

    private void addTextListener() {
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("TAG", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
            }
        });
    }

    @Override
    public int getLayoutResource() {
        return R.layout.name_add_fragment_page;
    }

    @Override
    public Bathroom getUpdatedBathroom(Bathroom initialBathroom) {
        initialBathroom.name = name;
        return initialBathroom;
    }
}
