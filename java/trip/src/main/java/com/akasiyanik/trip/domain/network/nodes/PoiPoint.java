package com.akasiyanik.trip.domain.network.nodes;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class PoiPoint extends GeoPoint {

    public PoiPoint(String name, Pair<Double, Double> latLng) {
        super(name, latLng);
    }

}

