package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.arcs.WalkArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.service.walk.WalkDistance;
import com.akasiyanik.trip.service.walk.repo.MongoWalkDistanceRepository;
import com.akasiyanik.trip.utils.TimeUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         6/1/17
 */
@Service
public class WalkingArcsGenerator {

    private static final Long MAX_DISTANCE = 2000L;

    @Autowired
    private MongoWalkDistanceRepository walkDistanceRepo;

    public List<BaseArc> generateArcs(InputParameters parameters, List<BaseArc> transportArcs) {

        String firstNodeId = parameters.getDeparturePointId();
        int departureTime = TimeUtils.timeToMinutes(parameters.getDepartureTime());
        int arrivalTime = TimeUtils.timeToMinutes(parameters.getArrivalTime());

        List<WalkDistance> walkDistances = walkDistanceRepo.findByDistance(MAX_DISTANCE);
//        List<WalkDistance> walkDistances = walkDistanceRepo.findAll();

        Multimap<String, WalkDistance> walkMap = ArrayListMultimap.create();
        walkDistances.stream().forEach(d -> {
            walkMap.put(d.getFirstNodeId(), d);
            walkMap.put(d.getSecondNodeId(), createDupWalkDistance(d));
        });

//        List<WalkArc> walkingArcs = new ArrayList<>();
        // simple
//        walkDistances.stream().forEach(d -> {
//            int minutes = d.getMinutes().intValue();
//            int time = departureTime;
//            while (time + minutes <= arrivalTime) {
//                walkingArcs.add(ArcFactory.getArc(Mode.WALK, d.getFirstNodeId(), time, d.getSecondNodeId(), time + minutes));
//                walkingArcs.add(ArcFactory.getArc(Mode.WALK, d.getSecondNodeId(), time, d.getFirstNodeId(), time + minutes));
//                time++;
//            }
//        });

//        walkMap.get(firstNodeId).stream();

        Set<BaseArc> walkingArcs = new HashSet<>();
        Set<BaseArc> transferArcs = new HashSet<>();

        Multimap<String, BaseNode> nodesByEnd = TreeMultimap.create(Ordering.natural(), (BaseNode n1, BaseNode n2) -> n1.getTime() - n2.getTime());
        transportArcs.stream().filter(a -> {
            Mode mode = a.getMode();
            return mode.isTransport() || mode.equals(Mode.DUMMY_START_FINISH);
        }).map(BaseArc::getJ).forEach(n -> nodesByEnd.put(n.getId(), n));

        Multimap<String, BaseNode> nodesByStart = TreeMultimap.create(Ordering.natural(), (BaseNode n1, BaseNode n2) -> n1.getTime() - n2.getTime());
        transportArcs.stream().filter(a -> {
            Mode mode = a.getMode();
            return mode.isTransport() || mode.equals(Mode.DUMMY_START_FINISH);
        }).map(BaseArc::getI).forEach(n -> nodesByStart.put(n.getId(), n));


        BaseNode depNode = new BaseNode(firstNodeId, departureTime);
        Collection<WalkDistance> distForDepNode = walkMap.get(firstNodeId);
        for (WalkDistance firstDist : distForDepNode) {
            int endTime = departureTime + firstDist.getMinutes().intValue();
            if (endTime <= arrivalTime) {
                BaseNode newNode = new BaseNode(firstDist.getSecondNodeId(), endTime);
                walkingArcs.add(new BaseArc(depNode, newNode, Mode.WALK));
                nodesByEnd.put(firstDist.getSecondNodeId(), newNode);

                Collection<BaseNode> startNodes = nodesByStart.get(newNode.getId());
                for (BaseNode startNode : startNodes) {
                    if (startNode.getTime() > newNode.getTime()) {
                        transferArcs.add(new BaseArc(newNode, startNode, Mode.TRANSFER));
                        break;
                    }
                }
            }
        }

        int walkingArcsPrevSize;
        int trasferArcsPrevSize;
        do {
            walkingArcsPrevSize = walkingArcs.size();
            trasferArcsPrevSize = transferArcs.size();

            Set<BaseNode> newNodes = new HashSet<>();
            for (String nodeId : nodesByEnd.keySet()) {
                for (BaseNode node : nodesByEnd.get(nodeId)) {
                    for (WalkDistance dist : walkMap.get(nodeId)) {
                        int endTime = node.getTime() + dist.getMinutes().intValue();
                        if (endTime <= arrivalTime) {
                            BaseNode newNode = new BaseNode(dist.getSecondNodeId(), endTime);
                            newNodes.add(newNode);
                            walkingArcs.add(new WalkArc(node, newNode));


                            Collection<BaseNode> startNodes = nodesByStart.get(newNode.getId());
                            for (BaseNode startNode : startNodes) {
                                if (startNode.getTime() > newNode.getTime()) {
                                    transferArcs.add(new BaseArc(newNode, startNode, Mode.TRANSFER));
                                        break;
                                }
                            }
                            //add Transfer Arc
                        }
                    }
                }

//                walkingArcs.stream().filter(a -> a.getI().getId().equals("592ecea0b929d5e7b04ff9cb") && a.getI().getTime() == 341 ).collect(Collectors.toList())
            }
            for (BaseNode node : newNodes) {
                nodesByEnd.put(node.getId(), node);
            }
//            nodesByEnd.putAll(nodeId, newNodes);

        } while (walkingArcsPrevSize != walkingArcs.size() || trasferArcsPrevSize != transferArcs.size());


        return new LinkedList<BaseArc>() {{
            addAll(transferArcs);
            addAll(walkingArcs);
        }};
    }

    private WalkDistance createDupWalkDistance(WalkDistance origin) {
        WalkDistance dup = new WalkDistance();
        dup.setId(origin.getId());
        dup.setMeters(origin.getMeters());
        dup.setMinutes(origin.getMinutes());
        dup.setNodesIds(Arrays.asList(origin.getSecondNodeId(), origin.getFirstNodeId()));
        return dup;
    }

    private List<BaseArc> getArcsByFirstNode(Collection<BaseArc> arcs, String id, String sTime) {
        int time = TimeUtils.timeToMinutes(sTime);
        return arcs.stream().filter(a -> a.getI().getId().equals(id) && a.getI().getTime() == time).collect(Collectors.toList());
    }
}
