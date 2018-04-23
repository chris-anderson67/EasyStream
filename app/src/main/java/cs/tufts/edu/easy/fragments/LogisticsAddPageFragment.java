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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cs.tufts.edu.easy.R;
import cs.tufts.edu.easy.activities.AddBathroomActivity;
import cs.tufts.edu.easy.models.Bathroom;

import static cs.tufts.edu.easy.R.id.add_bathroom_location_map;


public class LogisticsAddPageFragment extends BaseAddPageFragment implements OnMapReadyCallback {

    Location currentLocation = null;
    private double latitude = 0.0;
    private double longitude = 0.0;

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
        initialBathroom.latitude = latitude;
        initialBathroom.longitude = longitude;
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
                    .title("New Location")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) { }

                @Override
                public void onMarkerDrag(Marker marker) { }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    latitude = marker.getPosition().latitude;
                    longitude = marker.getPosition().longitude;
                }
            });
        }
    }
}
