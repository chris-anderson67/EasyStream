package cs.tufts.edu.easy.fragments;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.models.Bathroom;

import static cs.tufts.edu.easy.R.id.add_bathroom_location_map;


public class LogisticsAddPageFragment extends BaseAddPageFragment implements OnMapReadyCallback{
    CheckBox useCurrentLocation;
    EditText directions;

    @Override
    protected void setupViews(View rootView) {
        directions = (EditText) rootView.findViewById(R.id.logistics_fragment_directions);
        useCurrentLocation = (CheckBox) rootView.findViewById(R.id.logistics_fragment_current_location);
        
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(add_bathroom_location_map);
        mapFragment.getMapAsync(this);

        useCurrentLocation.setChecked(true);
        useCurrentLocation.setEnabled(false);
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
        return initialBathroom;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getActivity(), "map Ready", Toast.LENGTH_SHORT).show();
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
    }
}
