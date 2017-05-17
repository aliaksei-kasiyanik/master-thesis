package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.network.nodes.GeoPoint;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author akasiyanik
 *         5/10/17
 */
public class MetroPointsGenerator implements PointGenerator<GeoPoint> {

    private Map<String, Pair<Double, Double>> stations = new HashMap<String, Pair<Double, Double>>() {{
        put("Uruccha", new ImmutablePair<>(53.9277922,27.6540609));
        put("Barysauski trakt", new ImmutablePair<>(123.0, 123.0));
        put("Ushod", new ImmutablePair<>(123.0, 123.0));
        put("Maskouskaya", new ImmutablePair<>(123.0, 123.0));
        put("Park Chaliuskincau", new ImmutablePair<>(123.0, 123.0));
        put("Akademiya Navuk", new ImmutablePair<>(123.0, 123.0));
        put("Ploshcha Yacuba Kolasa", new ImmutablePair<>(123.0, 123.0));
        put("Ploscha Peramogi", new ImmutablePair<>(123.0, 123.0));
        put("Kastrychnickaya", new ImmutablePair<>(123.0, 123.0));
        put("Ploscha Lenina", new ImmutablePair<>(123.0, 123.0));
        put("Instytut Kultury", new ImmutablePair<>(123.0, 123.0));
        put("Hrushauka", new ImmutablePair<>(123.0, 123.0));
        put("Michalova", new ImmutablePair<>(123.0, 123.0));
        put("Piatroushcyna", new ImmutablePair<>(123.0, 123.0));
        put("Malinauka", new ImmutablePair<>(123.0, 123.0));
    }};


    @Override
    public Set<GeoPoint> generatePoints() {
        return null;
    }
}
