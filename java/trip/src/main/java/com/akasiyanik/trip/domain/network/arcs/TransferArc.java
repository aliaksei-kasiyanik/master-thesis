package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.network.nodes.BaseNode;

import static com.akasiyanik.trip.domain.Mode.TRANSFER;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class TransferArc extends BaseArc {

    public TransferArc(BaseNode i, BaseNode j) {
        super(i, j, TRANSFER);
        if (!i.getId().equals(j.getId())) {
            throw new RuntimeException("TransferArc must have the same BaseNodes id");
        }
    }
}
