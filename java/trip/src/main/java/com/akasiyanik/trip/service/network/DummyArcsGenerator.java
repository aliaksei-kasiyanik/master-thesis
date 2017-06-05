package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.arcs.DummyStartFinishArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         5/29/17
 */
@Component
public class DummyArcsGenerator {

    public List<DummyStartFinishArc> generateArcs(InputParameters parameters, Set<BaseArc> allArcs) {
        BaseNode startI = new BaseNode(parameters.getDeparturePointId(), parameters.getDepartureTime());
        BaseNode finishJ = new BaseNode(parameters.getArrivalPointId(), parameters.getArrivalTime());

        Set<DummyStartFinishArc> dummyArcs = new HashSet<>();
        Set<BaseNode> nodesInStartLocation = allArcs.stream().map(BaseArc::getI).filter(n -> n.getId().equals(startI.getId())).collect(Collectors.toSet());
        for (BaseNode node : nodesInStartLocation) {
            if (!node.equals(startI)) {
                dummyArcs.add(new DummyStartFinishArc(startI, node));
            }
        }

        Set<BaseNode> nodesInFinishLocation = allArcs.stream().map(BaseArc::getJ).filter(n -> n.getId().equals(finishJ.getId())).collect(Collectors.toSet());
        for (BaseNode node : nodesInFinishLocation) {
            if (!node.equals(finishJ)) {
                dummyArcs.add(new DummyStartFinishArc(node, finishJ));
            }
        }

        return new ArrayList<>(dummyArcs);
    }
}
