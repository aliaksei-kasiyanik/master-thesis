package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.GeoLocation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;

import java.util.*;

/**
 * @author akasiyanik
 *         5/10/17
 */
public class MinskTransStop {

    @Id
    private String id;

    private String name;

    private LinkedHashSet<Long> ids = new LinkedHashSet<>();

    private List<GeoLocation> locations = new ArrayList<>();

//    private Map<String, GeoLocation> idWithLocations;


    public MinskTransStop() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashSet<Long> getIds() {
        return ids;
    }

    public void setIds(LinkedHashSet<Long> ids) {
        this.ids = ids;
    }

    public List<GeoLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<GeoLocation> locations) {
        this.locations = locations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MinskTransStop that = (MinskTransStop) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "MinskTransStop{" +
                "name='" + name + '\'' +
                ", ids=" + ids +
                ", locations=" + locations +
                '}';
    }
}
