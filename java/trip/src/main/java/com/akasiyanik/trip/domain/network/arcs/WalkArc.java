package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;

/**
 * @author akasiyanik
 *         5/18/17
 */
public class WalkArc extends BaseArc {

    public WalkArc(BaseNode i, BaseNode j) {
        super(i, j, Mode.WALK);
    }
}
