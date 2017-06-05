package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.arcs.TransferArc;
import com.akasiyanik.trip.domain.network.arcs.VisitArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.utils.TimeUtils;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author akasiyanik
 *         6/5/17
 */
@Service
public class VisitArcsGenerator {

    public List<BaseArc> generateArcs(InputParameters parameters, Set<BaseArc> transportArcs) {
        Map<String, Integer> visitPoiDuration = parameters.getVisitPoi();
        Set<String> visitPoiIds = parameters.getVisitPoi().keySet();

        int arrivalTime = TimeUtils.timeToMinutes(parameters.getArrivalTime());

        Multimap<String, BaseNode> nodesByEnd = TreeMultimap.create(Ordering.natural(), (BaseNode n1, BaseNode n2) -> n1.getTime() - n2.getTime());
        transportArcs.stream().filter(a -> a.getMode().isTransport()).map(BaseArc::getJ).forEach(n -> nodesByEnd.put(n.getId(), n));

        Multimap<String, BaseNode> nodesByStart = TreeMultimap.create(Ordering.natural(), (BaseNode n1, BaseNode n2) -> n2.getTime() - n1.getTime());
        transportArcs.stream().filter(a -> a.getMode().isTransport()).map(BaseArc::getI).forEach(n -> nodesByStart.put(n.getId(), n));

        Set<BaseArc> visitArcs = new HashSet<>();
        Set<BaseArc> transferArcs = new HashSet<>();

        visitPoiIds.forEach(visitPoiId -> {
            List<BaseNode> visitStartNodes = new ArrayList<>(nodesByEnd.get(visitPoiId));
            List<BaseNode> transferEndNodes = new ArrayList<>(nodesByStart.get(visitPoiId));
            for (int i = 0; i < visitStartNodes.size(); i++) {

                BaseNode startNode = visitStartNodes.get(i);

                int endTime = startNode.getTime() + visitPoiDuration.get(visitPoiId);
                if (endTime <= arrivalTime) {
                    BaseNode endNode = new BaseNode(visitPoiId, endTime);
                    visitArcs.add(new VisitArc(startNode, endNode));

                    for (int j = 0; j < transferEndNodes.size(); j++) {

                        BaseNode transferEndNode = transferEndNodes.get(j);
                        if (transferEndNode.getTime() > endNode.getTime()) {
                            transferArcs.add(new TransferArc(endNode, transferEndNode));
                        } else {
                            break;
                        }
                    }
                } else {
                    break;
                }
            }

        });

        if (visitPoiIds.contains(parameters.getDeparturePointId())) {
            String visitPoiId = parameters.getDeparturePointId();
            int startTime = TimeUtils.timeToMinutes(parameters.getDepartureTime());
            int endTime = startTime + visitPoiDuration.get(visitPoiId);
            if (endTime <= arrivalTime) {

                BaseNode startNode = new BaseNode(visitPoiId, startTime);
                BaseNode endNode = new BaseNode(visitPoiId, endTime);

                visitArcs.add(new VisitArc(startNode, endNode));

                for (BaseNode transferEndNode : nodesByStart.get(visitPoiId)) {
                    if (transferEndNode.getTime() > endTime) {
                        transferArcs.add(new TransferArc(endNode, transferEndNode));
                    } else {
                        break;
                    }
                }

            }
        }

        List<BaseArc> result = new ArrayList<>();
        result.addAll(visitArcs);
        result.addAll(transferArcs);
        return result;


    }
}
