package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.network.nodes.BaseNode;

import static com.akasiyanik.trip.domain.TransportMode.VISIT;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class VisitArc extends BaseArc {

    public VisitArc(BaseNode i, BaseNode j) {
        super(i, j, VISIT);
        if (!i.getId().equals(j.getId())) {
            throw new RuntimeException("VisitArc must have the same BaseNodes id");
        }
    }

}