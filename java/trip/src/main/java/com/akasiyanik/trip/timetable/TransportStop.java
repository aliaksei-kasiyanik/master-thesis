package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.GeoLocation;
import org.springframework.data.annotation.Id;

import java.util.Set;

/**
 * @author akasiyanik
 *         5/31/17
 */
public class TransportStop {

    @Id
    private String id;

    private String name;

    private GeoLocation location;

    private Set<CrossRoute> crossRoutes;

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

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public Set<CrossRoute> getCrossRoutes() {
        return crossRoutes;
    }

    public void setCrossRoutes(Set<CrossRoute> crossRoutes) {
        this.crossRoutes = crossRoutes;
    }

}
