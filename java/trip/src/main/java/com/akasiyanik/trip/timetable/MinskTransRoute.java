package com.akasiyanik.trip.timetable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author akasiyanik
 *         5/10/17
 */
public class MinskTransRoute {

    private String routeNumber;

    private String routeName;

    private boolean reverse = false;

    private Map<String, String> stops;

    private List<List<String>> threads;

    public MinskTransRoute(String routeNumber, boolean reverse) {
        this.routeNumber = routeNumber;
        this.reverse = reverse;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public boolean isReverse() {
        return reverse;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public Map<String, String> getStops() {
        return stops;
    }

    public void setStops(Map<String, String> stops) {
        this.stops = stops;
    }

    public List<List<String>> getThreads() {
        return threads;
    }

    public void setThreads(List<List<String>> threads) {
        this.threads = threads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MinskTransRoute that = (MinskTransRoute) o;

        return new EqualsBuilder()
                .append(reverse, that.reverse)
                .append(routeNumber, that.routeNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(routeNumber)
                .append(reverse)
                .toHashCode();
    }
}
