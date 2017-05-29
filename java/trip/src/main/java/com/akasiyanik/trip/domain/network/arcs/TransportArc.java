package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;

/**
 * @author akasiyanik
 *         5/18/17
 */
public class TransportArc extends BaseArc {

    public TransportArc(BaseNode i, BaseNode j, Mode mode) {
        super(i, j, mode);
        if (!Mode.TRANSPORT.contains(mode)) {
            throw new RuntimeException("It isn't TRANSPORT mode");
        }
    }
}
