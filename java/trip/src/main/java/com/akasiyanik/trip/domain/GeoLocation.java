package com.akasiyanik.trip.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author akasiyanik
 *         5/12/17
 */
public class GeoLocation {

    private Double lat;
    private Double lon;

    public GeoLocation() {
    }

    public GeoLocation(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GeoLocation that = (GeoLocation) o;

        return new EqualsBuilder()
                .append(lat, that.lat)
                .append(lon, that.lon)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(lat)
                .append(lon)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "GeoLocation{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
