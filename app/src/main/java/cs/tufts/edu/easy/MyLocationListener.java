package cs.tufts.edu.easy;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by christopheranderson on 11/13/16.
 */
public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double longitude = location.getLongitude();
            Double latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
}


