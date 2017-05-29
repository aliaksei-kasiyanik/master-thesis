package com.akasiyanik.trip.domain.network.nodes;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class GeoPoint {

    private String name;

    private Pair<Double, Double> latLng;

    public GeoPoint(String name, Pair<Double, Double> latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public Pair<Double, Double> getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
