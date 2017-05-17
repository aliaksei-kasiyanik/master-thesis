package com.akasiyanik.trip.domain.network;

import com.akasiyanik.trip.domain.network.arcs.TransferArc;
import com.akasiyanik.trip.domain.network.arcs.VisitArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;

import java.util.Set;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class TripNetwork {

    private Set<BaseNode> nodes;

    private Set<VisitArc> visitArcs;

    private Set<TransferArc> transferArcs;



}
