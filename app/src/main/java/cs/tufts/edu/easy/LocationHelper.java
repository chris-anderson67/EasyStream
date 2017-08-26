package cs.tufts.edu.easy;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.Locale;

import cs.tufts.edu.easy.models.Bathroom;

public class LocationHelper {
    private static final String TAG = LocationHelper.class.getSimpleName();
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Adds location data to bathroom
     * @param context for geoCoder
     * @param bathroom to update
     * @param currentLocation to add to bathroom
     * @return updated bathroom
     */
    public static Bathroom populateWithCurrentLocation(Context context, Bathroom bathroom, Location currentLocation) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address address = null;

        try {
            address = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1 ).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address == null) {
            Log.e(TAG, "Error getting address from GeoCoder");
        } else {
            bathroom.street = "";
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                bathroom.street += address.getAddressLine(i); // process multiple lines addresses
                bathroom.street += " ";
            }
            bathroom.city = address.getLocality() + ", " + address.getAdminArea();
            bathroom.country = address.getCountryName();
        }

        bathroom.latitude = currentLocation.getLatitude();
        bathroom.longitude = currentLocation.getLongitude();
        return bathroom;
    }

    /** Determines whether one Location reading is better than the current Location fix
     *  Taken from android dev guide
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    public static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * @return True if location permission has been granted
     */
    public static boolean hasLocationPermission(Context context) {
            return !(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ||
                    (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * @param map to measure
     * @return radius in km
     */
    public static double getViewRadius(GoogleMap map) {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        CameraPosition center = map.getCameraPosition();
        float results[] = new float[1];

        Location.distanceBetween(center.target.latitude, center.target.longitude,
                bounds.northeast.latitude, bounds.northeast.longitude, results);
        return results[0] / 1000.00;
    }
}
