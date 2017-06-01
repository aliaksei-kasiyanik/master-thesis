package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.ArcFactory;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.arcs.TransferArc;
import com.akasiyanik.trip.domain.network.arcs.WalkArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.service.walk.WalkDistance;
import com.akasiyanik.trip.service.walk.repo.MongoWalkDistanceRepository;
import com.akasiyanik.trip.utils.TimeUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author akasiyanik
 *         6/1/17
 */
@Service
public class WalkingArcsGenerator {

    @Autowired
    private MongoWalkDistanceRepository walkDistanceRepo;

    public List<BaseArc> generateArcs(InputParameters parameters, List<BaseArc> transportArcs) {

        String firstNodeId = parameters.getDeparturePointId();
        int departureTime = TimeUtils.timeToMinutes(parameters.getDepartureTime());
        int arrivalTime = TimeUtils.timeToMinutes(parameters.getArrivalTime());

        List<WalkDistance> walkDistances = walkDistanceRepo.findAll();
//        Map<Set<String>, WalkDistance> walkMap = new HashMap<>();
//        walkDistances.stream().forEach(d -> {
//            walkMap.put(new HashSet<>(Arrays.asList(d.getFirstNodeId(), d.getSecondNodeId())), d);
//        });

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
        transportArcs.stream().filter(a -> a.getMode().isTransport()).map(BaseArc::getJ).forEach(n -> nodesByEnd.put(n.getId(), n));

        Multimap<String, BaseNode> nodesByStart = TreeMultimap.create(Ordering.natural(), (BaseNode n1, BaseNode n2) -> n1.getTime() - n2.getTime());
        transportArcs.stream().filter(a -> a.getMode().isTransport()).map(BaseArc::getI).forEach(n -> nodesByStart.put(n.getId(), n));

        int walkingArcsPrevSize = 0;
        int trasferArcsPrevSize = 0;
        do {
            walkingArcsPrevSize = walkingArcs.size();
            trasferArcsPrevSize = transferArcs.size();

            for (String nodeId : nodesByEnd.keySet()) {

                Collection<WalkDistance> distances = walkMap.get(nodeId);

                if (CollectionUtils.isNotEmpty(distances)) {
                    Collection<BaseNode> nodes = nodesByEnd.get(nodeId);
                    Set<BaseNode> newNodes = new HashSet<>();
                    for (BaseNode node : nodes) {
                        for (WalkDistance dist : distances) {
                            int endTime = node.getTime() + dist.getMinutes().intValue();
                            if (endTime <= arrivalTime) {
                                BaseNode newNode  = new BaseNode(dist.getSecondNodeId(), endTime);
                                newNodes.add(newNode);
                                walkingArcs.add(new BaseArc(node, newNode, Mode.WALK));


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
                    nodesByEnd.putAll(nodeId, newNodes);
                }


            }

        } while (walkingArcsPrevSize != walkingArcs.size() || trasferArcsPrevSize != transferArcs.size());



//        Map<String, List<BaseNode>> nodesByStart = transportArcs.stream().map(BaseArc::getI).collect(Collectors.groupingBy(BaseNode::getId));
//        Map<String, List<BaseNode>> nodesByFinish = transportArcs.stream().map(BaseArc::getJ).collect(Collectors.groupingBy(BaseNode::getId));


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
}
