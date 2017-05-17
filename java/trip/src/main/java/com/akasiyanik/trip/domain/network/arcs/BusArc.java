package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.network.nodes.BaseNode;

import static com.akasiyanik.trip.domain.TransportMode.BUS;

/**
 * @author akasiyanik
 *         5/12/17
 */
public class BusArc extends BaseArc {

    public BusArc(BaseNode i, BaseNode j) {
        super(i, j, BUS);
    }
}
