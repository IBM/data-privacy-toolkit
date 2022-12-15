/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.providers.identifiers.LatitudeLongitudeIdentifier;

public class GeoUtils {
    private final static Double R = 6378137.0;
    private final static LatitudeLongitudeIdentifier latitudeLongitudeIdentifier = new LatitudeLongitudeIdentifier();

    private final static double asq = Math.pow(R, 2);
    private final static double e = 8.1819190842622e-2;
    private final static double esq = Math.pow(e, 2);


    public static Double getR() {
        return R;
    }

    public static LatitudeLongitude xyzToLatlon(double x, double y, double z) {
        double b = Math.sqrt(asq * (1 - esq));
        double bsq = Math.pow(b, 2);
        double ep = Math.sqrt((asq - bsq) / bsq);
        double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double th = Math.atan2(R * z, b * p);

        double lon = Math.atan2(y, x);
        double lat = Math.atan2((z + Math.pow(ep, 2) * b * Math.pow(Math.sin(th), 3)), (p - esq * R * Math.pow(Math.cos(th), 3)));
        double N = R / (Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2)));
        double alt = p / Math.cos(lat) - N;

        // mod lat to 0-2pi
        lon = lon % (2 * Math.PI);

        // correction for altitude near poles left out.
        return new LatitudeLongitude(Math.toDegrees(lat), Math.toDegrees(lon));
    }

    public static XYZ latlonToXYZ(double lat, double lon) {
        double cosLat = Math.cos(lat * Math.PI / 180.0);
        double sinLat = Math.sin(lat * Math.PI / 180.0);
        double cosLon = Math.cos(lon * Math.PI / 180.0);
        double sinLon = Math.sin(lon * Math.PI / 180.0);
        double rad = R;
        double f = 1.0 / 298.257224;
        double C = 1.0 / Math.sqrt(cosLat * cosLat + (1 - f) * (1 - f) * sinLat * sinLat);
        double S = (1.0 - f) * (1.0 - f) * C;
        double h = 0.0;

        double x = (rad * C + h) * cosLat * cosLon;
        double y = (rad * C + h) * cosLat * sinLon;
        double z = (rad * S + h) * sinLat;

        return new XYZ(x, y, z);
    }

    /**
     * Degrees to decimal double.
     *
     * @param degrees the degrees
     * @param minutes the minutes
     * @param seconds the seconds
     * @return the double
     */
    public static double degreesToDecimal(double degrees, double minutes, double seconds) {
        return degrees + minutes / 60 + seconds / 3600;
    }

    /**
     * Latitude longitude distance double.
     *
     * @param lat1 the lat 1
     * @param lon1 the lon 1
     * @param lat2 the lat 2
     * @param lon2 the lon 2
     * @return the double
     */
    public static double latitudeLongitudeDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Latitude longitude distance double.
     *
     * @param latlon1 the latlon 1
     * @param latlon2 the latlon 2
     * @return the double
     */
    public static double latitudeLongitudeDistance(LatitudeLongitude latlon1, LatitudeLongitude latlon2) {
        return latitudeLongitudeDistance(latlon1.getLatitude(), latlon1.getLongitude(),
                latlon2.getLatitude(), latlon2.getLongitude());
    }
}
