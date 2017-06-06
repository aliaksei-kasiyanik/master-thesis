package com.akasiyanik.trip.cplex.solution;

import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;

import java.util.List;

/**
 * @author akasiyanik
 *         6/6/17
 */
public class ProblemSolution {

    private double objectiveValue;

    private List<BaseArc> route;

    private long time; // millisec

    private RouteCriteria criteria;

    private Double epsilon;

    private long constraintsCount;

    public long getConstraintsCount() {
        return constraintsCount;
    }

    public void setConstraintsCount(long constraintsCount) {
        this.constraintsCount = constraintsCount;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public void setObjectiveValue(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public List<BaseArc> getRoute() {
        return route;
    }

    public void setRoute(List<BaseArc> route) {
        this.route = route;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public RouteCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(RouteCriteria criteria) {
        this.criteria = criteria;
    }

    public Double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(Double epsilon) {
        this.epsilon = epsilon;
    }
}
