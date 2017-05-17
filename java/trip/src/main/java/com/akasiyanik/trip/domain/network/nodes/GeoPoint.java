package com.akasiyanik.trip.domain.network.nodes;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class GeoPoint {

    private Long id;

    private Pair<Double, Double> latLng;

    private String name;

    public GeoPoint(Long id, Pair<Double, Double> latLng) {
        this.id = id;
        this.latLng = latLng;
    }

    public Long getId() {
        return id;
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
