package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.network.nodes.GeoPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class GeoPointGenerator implements PointGenerator<GeoPoint> {

    // generate bus stops, metro stations in area

    public Set<GeoPoint> generatePoints() {
        return new HashSet<>();
    }
}
