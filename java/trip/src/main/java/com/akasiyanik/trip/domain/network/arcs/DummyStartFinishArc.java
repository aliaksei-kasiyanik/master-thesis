package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.network.nodes.BaseNode;

import static com.akasiyanik.trip.domain.TransportMode.DUMMY_START_FINISH;

/**
 *
 * This is introduced to address min departure and maximum arrival times of journey.
 * @author akasiyanik
 *         5/5/17
 */
public class DummyStartFinishArc extends BaseArc {

    public DummyStartFinishArc(BaseNode i, BaseNode j) {
        super(i, j, DUMMY_START_FINISH);
        if (!i.getId().equals(j.getId())) {
            throw new RuntimeException("VisitArc must have the same BaseNodes id");
        }
    }

}
