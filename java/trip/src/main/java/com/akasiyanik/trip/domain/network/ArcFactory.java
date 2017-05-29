package com.akasiyanik.trip.domain.network;

import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.arcs.*;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;

/**
 * @author akasiyanik
 *         5/22/17
 */
public class ArcFactory {

    public static BaseArc getArc(Mode mode, String i, int a, String j, int b) {
        BaseNode first = new BaseNode(i, a);
        BaseNode second = new BaseNode(j, b);
        switch (mode) {
            case TRANSFER: {
                return new TransferArc(first, second);
            }
            case VISIT: {
                return new VisitArc(first, second);
            }
            case DUMMY_START_FINISH: {
                return new DummyStartFinishArc(first, second);
            }
            case WALK: {
                return new WalkArc(first, second);
            }
            default: {
                return new TransportArc(first, second, mode);
            }
        }
    }
}
