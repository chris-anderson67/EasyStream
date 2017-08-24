package cs.tufts.edu.easy.fragments;

import android.app.Activity;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.activities.AddBathroomActivity;
import cs.tufts.edu.easy.models.Bathroom;

import static cs.tufts.edu.easy.R.id.add_bathroom_location_map;


public class LogisticsAddPageFragment extends BaseAddPageFragment implements OnMapReadyCallback{

    Location currentLocation = null;

    @Override
    protected void setupViews(View rootView) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(add_bathroom_location_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.logistics_add_fragment_page;
    }

    @Override
    public Bathroom getUpdatedBathroom(Bathroom initialBathroom) {

        return initialBathroom;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getActivity(), "map Ready", Toast.LENGTH_SHORT).show();
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));

        Activity activity = getActivity();
        AddBathroomActivity addActivity = null;
        if (activity != null && activity instanceof AddBathroomActivity) {
            addActivity = (AddBathroomActivity) activity;
            currentLocation = addActivity.getCurrentLocation();
            if (currentLocation == null) {
                Toast.makeText(addActivity, "couldnt find current location", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
        }
    }
}
