package cs.tufts.edu.easy.fragments;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.models.Bathroom;


public class LogisticsAddPageFragment extends BaseAddPageFragment {
    CheckBox unisex;
    CheckBox changingTable;
    CheckBox handicapAccessible;
    EditText directions;

    @Override
    protected void setupViews(View rootView) {
        unisex = (CheckBox) rootView.findViewById(R.id.logistics_fragment_unisex);
        changingTable = (CheckBox) rootView.findViewById(R.id.logistics_fragment_changing_table);
        handicapAccessible = (CheckBox) rootView.findViewById(R.id.logistics_fragment_accessible);
        directions = (EditText) rootView.findViewById(R.id.logistics_fragment_directions);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.logistics_add_fragment_page;
    }

    @Override
    public Bathroom getUpdatedBathroom(Bathroom initialBathroom) {
        if (directions.getText().toString().equals("")) {
            return null;
        }

        initialBathroom.directions = directions.getText().toString();
        initialBathroom.changing_table = changingTable.isChecked();
        initialBathroom.accessible = handicapAccessible.isChecked();
        initialBathroom.unisex = unisex.isChecked();
        return initialBathroom;
    }
}
