package com.akasiyanik.trip.utils;

import com.akasiyanik.trip.timetable.TransportStop;

/**
 * @author akasiyanik
 *         6/1/17
 */
public final class GeoUtils {

    public static double getDistanceInMeters(TransportStop stop1, TransportStop stop2) {
        return getDistanceInMeters(stop1.getLocation().getLat(), stop1.getLocation().getLon(), stop2.getLocation().getLat(), stop2.getLocation().getLon());
    }


    public static double getDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist * 1609.344; // in meters

    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
