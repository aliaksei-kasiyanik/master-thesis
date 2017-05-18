package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.TripNetwork;
import com.akasiyanik.trip.domain.network.nodes.GeoPoint;

import java.util.Optional;
import java.util.Set;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class NetworkGenerationService {

    private GeoPointGenerator geoPointGenerator;


    public TripNetwork generateNetwork(InputParameters tripParameters) {

        Set<GeoPoint> points = geoPointGenerator.generatePoints();
        GeoPoint startPoint = getPointById(points, tripParameters.getDeparturePointId());
        GeoPoint finishPoint = getPointById(points, tripParameters.getDeparturePointId());

        for (Mode mode : tripParameters.getModes()) {

        }
//        Set<BaseArc>





        return null;
    }

    private GeoPoint getPointById(Set<GeoPoint> points, Long departurePointId) {
        Optional<GeoPoint> point = points.stream().filter(p -> p.getId().equals(departurePointId)).findFirst();
        if (point.isPresent()) {
            return point.get();
        } else {
            throw new RuntimeException("Point with id[" + departurePointId + "] doesn't exist");
        }
    }


}
