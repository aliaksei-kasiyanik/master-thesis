package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.network.nodes.BaseNode;

import static com.akasiyanik.trip.domain.TransportMode.METRO;

/**
 * @author akasiyanik
 *         5/18/17
 */
public class MetroArc extends BaseArc {

    public MetroArc(BaseNode i, BaseNode j) {
        super(i, j, METRO);
    }

}
