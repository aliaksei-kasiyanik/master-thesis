package com.akasiyanik.trip.domain;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class InputParameters {

    // i*
    private final Long departurePointId;

    // j*
    private final Long arrivalPointId;

    // s*
    private final LocalTime departureTime;

    // t*
    private final LocalTime arrivalTime;

    // keys - set I - desirable POIs
    // values - visiting durations
    private final Map<Long, Integer> visitPoi;

    // eligible transport nodes
    private final Set<TransportMode> modes;

    // ranked criteria and relative deviations
    private final LinkedHashMap<RouteCriteria, Double> criteria;

    public InputParameters(Long departurePointId,
                           Long arrivalPointId,
                           LocalTime departureTime,
                           LocalTime arrivalTime,
                           Set<TransportMode> modes,
                           Map<Long, Integer> visitPoi,
                           LinkedHashMap<RouteCriteria, Double> criteria) {
        this.criteria = criteria;
        this.modes = modes;
        this.visitPoi = visitPoi;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.arrivalPointId = arrivalPointId;
        this.departurePointId = departurePointId;
    }

    public Long getDeparturePointId() {
        return departurePointId;
    }

    public Long getArrivalPointId() {
        return arrivalPointId;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public Map<Long, Integer> getVisitPoi() {
        return visitPoi;
    }

    public Set<TransportMode> getModes() {
        return modes;
    }

    public LinkedHashMap<RouteCriteria, Double> getCriteria() {
        return criteria;
    }
}
