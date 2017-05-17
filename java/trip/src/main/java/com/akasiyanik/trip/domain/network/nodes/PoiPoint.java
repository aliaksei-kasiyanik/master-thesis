package com.akasiyanik.trip.domain.network.nodes;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class PoiPoint extends GeoPoint {

    public PoiPoint(Long id, Pair<Double, Double> latLng) {
        super(id, latLng);
    }

}

