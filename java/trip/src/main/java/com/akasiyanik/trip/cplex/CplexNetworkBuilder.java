package com.akasiyanik.trip.cplex;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.network.arcs.*;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         5/12/17
 */
public class CplexNetworkBuilder {

    private InputParameters parameters;

    public CplexNetworkBuilder(InputParameters parameters) {
        this.parameters = parameters;
    }

    public List<BaseArc> build() {


        List<BaseArc> arcs = getNetworkArcs();

        BaseNode startI = new BaseNode(parameters.getDeparturePointId(), parameters.getDepartureTime());
        BaseNode finishJ = new BaseNode(parameters.getArrivalPointId(), parameters.getArrivalTime());

        createDummyArcs(arcs, startI, finishJ);

        return arcs;
    }

    private void createDummyArcs(List<BaseArc> arcs, BaseNode startI, BaseNode finishJ) {

        Set<BaseNode> nodesInStartLocation = arcs.stream().map(BaseArc::getI).filter(n -> n.getId().equals(startI.getId())).collect(Collectors.toSet());
        for (BaseNode node : nodesInStartLocation) {
            if (!node.equals(startI)) {
                arcs.add(new DummyStartFinishArc(startI, node));
            }
        }

        Set<BaseNode> nodesInFinishLocation = arcs.stream().map(BaseArc::getJ).filter(n -> n.getId().equals(finishJ.getId())).collect(Collectors.toSet());
        for (BaseNode node : nodesInFinishLocation) {
            if (!node.equals(finishJ)) {
                arcs.add(new DummyStartFinishArc(node, finishJ));
            }
        }

    }

    private List<BaseArc> getNetworkArcs() {

        Map<String, Long> places = new HashMap<String, Long>() {{
            put("a", 1L);
            put("b", 2L);
            put("c", 3L);
            put("d", 4L);
            put("e", 5L);
            put("f", 6L);
            put("g", 7L);
            put("h", 8L);
            put("k", 9L);
            put("l", 10L);
            put("m", 11L);
            put("p", 12L);
        }};

        return new ArrayList<BaseArc>() {{
            add(new BusArc(
                    new BaseNode(places.get("a"), 3),
                    new BaseNode(places.get("b"), 4)
            ));
            // center branch
            add(new BusArc(
                    new BaseNode(places.get("b"), 4),
                    new BaseNode(places.get("f"), 6)
            ));
            add(new BusArc(
                    new BaseNode(places.get("f"), 6),
                    new BaseNode(places.get("g"), 10)
            ));
            add(new VisitArc(
                    new BaseNode(places.get("g"), 10),
                    new BaseNode(places.get("g"), 15)
            ));
            add(new BusArc(
                    new BaseNode(places.get("g"), 15),
                    new BaseNode(places.get("k"), 20)
            ));
            add(new BusArc(
                    new BaseNode(places.get("g"), 15),
                    new BaseNode(places.get("h"), 19)
            ));
            add(new BusArc(
                    new BaseNode(places.get("h"), 19),
                    new BaseNode(places.get("k"), 30)
            ));
            add(new BusArc(
                    new BaseNode(places.get("g"), 10),
                    new BaseNode(places.get("h"), 14)
            ));
            add(new BusArc(
                    new BaseNode(places.get("h"), 14),
                    new BaseNode(places.get("k"), 25)
            ));

            //right branch
            add(new TransferArc(
                    new BaseNode(places.get("f"), 6),
                    new BaseNode(places.get("f"), 7)
            ));
            add(new MetroArc(
                    new BaseNode(places.get("f"), 7),
                    new BaseNode(places.get("l"), 8)
            ));
            add(new VisitArc(
                    new BaseNode(places.get("l"), 8),
                    new BaseNode(places.get("l"), 10)
            ));
            add(new MetroArc(
                    new BaseNode(places.get("l"), 10),
                    new BaseNode(places.get("m"), 11)
            ));
            add(new VisitArc(
                    new BaseNode(places.get("m"), 11),
                    new BaseNode(places.get("m"), 13)
            ));
            add(new MetroArc(
                    new BaseNode(places.get("m"), 13),
                    new BaseNode(places.get("p"), 19)
            ));
            add(new VisitArc(
                    new BaseNode(places.get("p"), 19),
                    new BaseNode(places.get("p"), 21)
            ));
            add(new MetroArc(
                    new BaseNode(places.get("p"), 21),
                    new BaseNode(places.get("h"), 24)
            ));
            add(new TransferArc(
                    new BaseNode(places.get("h"), 24),
                    new BaseNode(places.get("h"), 25)
            ));
            add(new BusArc(
                    new BaseNode(places.get("h"), 25),
                    new BaseNode(places.get("k"), 26)
            ));

            //left branch
            add(new BusArc(
                    new BaseNode(places.get("b"), 4),
                    new BaseNode(places.get("c"), 5)
            ));
            add(new VisitArc(
                    new BaseNode(places.get("c"), 5),
                    new BaseNode(places.get("c"), 10)
            ));
            add(new BusArc(
                    new BaseNode(places.get("c"), 10),
                    new BaseNode(places.get("d"), 13)
            ));
            add(new BusArc(
                    new BaseNode(places.get("d"), 13),
                    new BaseNode(places.get("e"), 17)
            ));
            add(new VisitArc(
                    new BaseNode(places.get("e"), 17),
                    new BaseNode(places.get("e"), 25)
            ));
            add(new BusArc(
                    new BaseNode(places.get("e"), 25),
                    new BaseNode(places.get("k"), 30)
            ));
            add(new BusArc(
                    new BaseNode(places.get("c"), 5),
                    new BaseNode(places.get("d"), 8)
            ));
            add(new BusArc(
                    new BaseNode(places.get("d"), 8),
                    new BaseNode(places.get("e"), 12)
            ));
            add(new BusArc(
                    new BaseNode(places.get("e"), 12),
                    new BaseNode(places.get("k"), 19)
            ));
            add(new BusArc(
                    new BaseNode(places.get("e"), 17),
                    new BaseNode(places.get("k"), 22)
            ));
        }};
    }


}
