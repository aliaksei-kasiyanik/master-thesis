package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Type;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.utils.TimeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author akasiyanik
 *         5/5/17
 */
@Service
public class NetworkGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkGenerationService.class);

    private static int TIME = TimeUtils.timeToMinutes("9:43");

    @Autowired
    private MinskTransNetworkGenerator minskTransNetworkGenerator;

    @Autowired
    private DummyArcsGenerator dummyArcsGenerator;

    @Autowired
    private WalkingArcsGenerator walkingArcsGenerator;

    @Autowired
    private VisitArcsGenerator visitArcsGenerator;

    public List<BaseArc> generateNetwork(InputParameters parameters) {

        Set<BaseArc> allArcs = new HashSet<>();

        allArcs.addAll(minskTransNetworkGenerator.generateArcs(parameters));
        allArcs.addAll(visitArcsGenerator.generateArcs(parameters, allArcs));
        if (parameters.getModes().contains(Type.WALK)) {
            allArcs.addAll(walkingArcsGenerator.generateArcs(parameters, allArcs));
        }
        allArcs.addAll(dummyArcsGenerator.generateArcs(parameters, allArcs));
//        allArcs = removeUnreacheableArcs(allArcs, parameters);
        List<BaseArc> result = new ArrayList<>(allArcs);
        return result;
    }

    private Set<BaseArc> removeUnreacheableArcs(Set<BaseArc> allArcs, InputParameters parameters) {
        BaseNode startPoint = new BaseNode(parameters.getDeparturePointId(), parameters.getDepartureTime());
        BaseNode finishPoint = new BaseNode(parameters.getArrivalPointId(), parameters.getArrivalTime());
        Set<BaseNode> nodes = new HashSet<>();
        for (BaseArc arc : allArcs) {
             nodes.add(arc.getI());
             nodes.add(arc.getJ());
        }
        nodes.remove(startPoint);
        nodes.remove(finishPoint);


        Map<BaseNode, Set<BaseArc>> incArcs = getIncomingArcsByNodes(allArcs);
        Map<BaseNode, Set<BaseArc>> outArcs = getOutgoingArcsByNodes(allArcs);

        ;

        int nodesCount = 0;
        int arcsCount = 0;
        int prevArcsSize;
        do {
            prevArcsSize = allArcs.size();

            Set<BaseNode> removedNodes = new HashSet<>();
            Set<BaseArc> removedArcs = new HashSet<>();

            for (BaseNode node : nodes) {
                Set<BaseArc> in = incArcs.get(node);
                Set<BaseArc> out = outArcs.get(node);

                if (CollectionUtils.isEmpty(in)) {
                    arcsCount += out.size();
                    removedArcs.addAll(out);
                    removedNodes.add(node);
                    continue;
                }

                if (CollectionUtils.isEmpty(out)) {
                    arcsCount += in.size();
                    removedArcs.addAll(in);
                    removedNodes.add(node);
                }
            }

            nodes.removeAll(removedNodes);
            allArcs.removeAll(removedArcs);
            nodesCount += removedNodes.size();
            arcsCount += removedArcs.size();

            for (Set<BaseArc> arcs : incArcs.values()) {
                arcs.removeIf(removedArcs::contains);
            }

            for (Set<BaseArc> arcs : outArcs.values()) {
                arcs.removeIf(removedArcs::contains);
            }


        } while (prevArcsSize != allArcs.size());

        logger.info("nodesCount = {}", nodesCount);
        logger.info("arcsCount = {}", arcsCount);



//        allArcs = allArcs.parallelStream().filter(a -> {
//            return !(a.getJ().getId().equals(parameters.getDeparturePointId())
//            a.getJ().getTime() >= time;
//        }).collect(Collectors.toList());
        return allArcs;
    }

    private Map<BaseNode, Set<BaseArc>> getOutgoingArcsByNodes(Set<BaseArc> arcs) {
        Map<BaseNode, Set<BaseArc>> outgoingArcs = new HashMap<>();
        for (BaseArc arc : arcs) {
            // out
            BaseNode arcI = arc.getI();
            Set<BaseArc> out = outgoingArcs.get(arcI);
            if (out == null) {
                out = new HashSet<>();
                outgoingArcs.put(arcI, out);
            }
            out.add(arc);
        }
        return outgoingArcs;
    }

    private Map<BaseNode, Set<BaseArc>> getIncomingArcsByNodes(Set<BaseArc> arcs) {
        Map<BaseNode, Set<BaseArc>> ingoingArcs = new HashMap<>();
        for (BaseArc arc : arcs) {
            // out
            BaseNode arcJ = arc.getJ();
            Set<BaseArc> in = ingoingArcs.get(arcJ);
            if (in == null) {
                in = new HashSet<>();
                ingoingArcs.put(arcJ, in);
            }
            in.add(arc);
        }
        return ingoingArcs;
    }

}
