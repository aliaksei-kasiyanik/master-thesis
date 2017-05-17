package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.GeoLocation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author akasiyanik
 *         5/10/17
 */
public class MinskTransStop {

    //from MinskTrans
//    private String id;

    private String name;

    private Map<String, GeoLocation> idWithLocations;

    public MinskTransStop(String name) {
        this.name = name;
        idWithLocations = new HashMap<>();
    }

    public MinskTransStop(Map<String, GeoLocation> idWithLocations, String name) {
        this.idWithLocations = idWithLocations;
        this.name = name;
    }

    public Map<String, GeoLocation> getIdWithLocations() {
        return idWithLocations;
    }

    public String getName() {
        return name;
    }

    public Set<String> getIds() {
        return idWithLocations.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MinskTransStop that = (MinskTransStop) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(idWithLocations, that.idWithLocations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(idWithLocations)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "MinskTransStop{" +
                "name='" + name + '\'' +
                ", idWithLocations=" + idWithLocations +
                '}';
    }
}
