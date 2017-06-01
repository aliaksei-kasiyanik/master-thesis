package com.akasiyanik.trip.service.walk;

import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @author akasiyanik
 *         6/1/17
 */
public class WalkDistance {

    @Id
    private String id;

    private List<String> nodesIds;

    private Long meters;

    private Long minutes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getNodesIds() {
        return nodesIds;
    }

    public void setNodesIds(List<String> nodesIds) {
        this.nodesIds = nodesIds;
    }

    public String getFirstNodeId() {
        return nodesIds.get(0);
    }

    public String getSecondNodeId() {
        return nodesIds.get(1);
    }

    public Long getMeters() {
        return meters;
    }

    public void setMeters(Long meters) {
        this.meters = meters;
    }

    public Long getMinutes() {
        return minutes;
    }

    public void setMinutes(Long minutes) {
        this.minutes = minutes;
    }
}
