package com.akasiyanik.trip.domain;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class InputParameters {

    // i*
    private final String departurePointId;

    // j*
    private final String arrivalPointId;

    // s*
    private final LocalTime departureTime;

    // t*
    private final LocalTime arrivalTime;

    // keys - set I - desirable POIs
    // values - visiting durations
    private final Map<String, Integer> visitPoi;

    // eligible transport nodes
    private final Set<Type> modes;

    // ranked criteria and relative deviations
    private final List<Pair<RouteCriteria, Double>> criteria;

    public InputParameters(String departurePointId,
                           String arrivalPointId,
                           LocalTime departureTime,
                           LocalTime arrivalTime,
                           Set<Type> modes,
                           Map<String, Integer> visitPoi,
                           List<Pair<RouteCriteria, Double>> criteria) {
        this.criteria = criteria;
        this.modes = modes;
        this.visitPoi = visitPoi;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.arrivalPointId = arrivalPointId;
        this.departurePointId = departurePointId;
    }

    public String getDeparturePointId() {
        return departurePointId;
    }

    public String getArrivalPointId() {
        return arrivalPointId;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public Map<String, Integer> getVisitPoi() {
        return visitPoi;
    }

    public Set<Type> getModes() {
        return modes;
    }

    public List<Pair<RouteCriteria, Double>> getCriteria() {
        return criteria;
    }
}
