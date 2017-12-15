package ca.projectpc.projectpc.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

public class LatLong {
    public static Location getLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return null;
    }

    public static double getDistanceBetweenLocations(double latitude1, double longitude1,
                                                     double latitude2, double longitude2) {
        double latitude = Math.toRadians(latitude2 - latitude1);
        double longitude = Math.toRadians(longitude2 - longitude1);

        double a = Math.pow(Math.sin(latitude / 2), 2)
                + Math.cos(Math.toRadians(latitude1))
                * Math.cos(Math.toRadians(latitude2))
                * Math.pow(Math.sin(longitude / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Radius of Earth * c * 1000
        return 6378.1 * c * 1000; // Meters
    }
}
