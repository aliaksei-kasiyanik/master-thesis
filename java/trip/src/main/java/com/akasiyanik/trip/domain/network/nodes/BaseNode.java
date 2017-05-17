package com.akasiyanik.trip.domain.network.nodes;

import com.akasiyanik.trip.utils.TimeUtils;

import java.time.LocalTime;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class BaseNode {

    private final Long id;

    private final int time;

    private GeoPoint geoLocation;

    public BaseNode(Long id, LocalTime time) {
        this.id = id;
        this.time = TimeUtils.timeToMinutes(time);
    }

    public BaseNode(Long id, int time) {
        this.id = id;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public LocalTime getLocalTime() {
        return TimeUtils.minutesToTime(time);
    }

    public GeoPoint getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoPoint geoLocation) {
        this.geoLocation = geoLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseNode baseNode = (BaseNode) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(id, baseNode.id)
                .append(time, baseNode.time)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(id)
                .append(time)
                .toHashCode();
    }
}
