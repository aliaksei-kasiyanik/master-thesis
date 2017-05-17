package com.akasiyanik.trip.cplex;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.TransportMode;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.arcs.BusArc;
import com.akasiyanik.trip.domain.network.arcs.VisitArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.utils.TimeUtils;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.akasiyanik.trip.utils.CplexUtil.*;

/**
 * @author akasiyanik
 *         5/5/17
 */
@Component
public class TripRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProblemSolver.class);

    public static void main(String[] args) {

        List<BaseArc> arcs = getNetworkArcs();
        InputParameters parameters = getFakeInputParameters();

        ProblemSolver solver = new ProblemSolver(arcs);
        List<BaseArc> result = solver.solve(parameters);

        logger.info("{}", result);


//        new TripRunner().start();
    }

    public List<BaseArc> start() {
        InputParameters tripParameters = getFakeInputParameters();

        List<BaseArc> arcs = getNetworkArcs();


        BaseNode startI = new BaseNode(1L, 1);
        BaseNode finishJ = new BaseNode(9L, 30);
//        int I_index = IntStream.range(0, arcs.size())
//                .filter(i -> arcs.get(i).getI().equals(startI))
//                .findFirst().getAsInt();
//        int J_index = IntStream.range(0, arcs.size())
//                .filter(i -> arcs.get(i).getJ().equals(finishJ))
//                .findFirst().getAsInt();


        int[] visitArcsMask = new int[arcs.size()];
        Map<Long, List<Integer>> visitArcsByLocation = IntStream
                .range(0, arcs.size())
                .filter(i -> arcs.get(i).getMode().equals(TransportMode.VISIT))
                .peek(i -> visitArcsMask[i] = 1)
                .boxed()
                .collect(Collectors.groupingBy(i -> arcs.get(i).getI().getId()));

        Map<BaseNode, Set<Integer>> outgoingArcs = getOutgoingArcsByNodes(arcs);
        Map<BaseNode, Set<Integer>> incomingArcs = getIncomingArcsByNodes(arcs);

        Set<Integer> startArcs = outgoingArcs.remove(startI);
        Set<Integer> finishArcs = incomingArcs.remove(finishJ);

        try {
            IloCplex model = new IloCplex();

            IloIntVar[] x = model.boolVarArray(arcs.size());

            //    Maximize VISIT arcs count
            addObjectiveFunction(model, x, visitArcsMask);

            //constraints (3) - (4)
            addUniqueInOutArcsConstraint(model, x, incomingArcs);
            addUniqueInOutArcsConstraint(model, x, outgoingArcs);

            //constraints (1) - (2)
            addOnlyInOutArcConstraint(model, x, startArcs);
            addOnlyInOutArcConstraint(model, x, finishArcs);

            // constraint (5)
            addEqualInOutForIntermediateNodesConstraint(model, x, outgoingArcs, incomingArcs);

            //constraint(6)
            //TODO add transfer mode constraints

            //constraint (7)
            addAtMostOneVisitArcForLocationConstraint(model, x, visitArcsByLocation);

            //constraint (8)
            addStartOnlyInSpecifiedLocationConstraint(model, x, outgoingArcs, startI);

            //constraint(9)
            //TODO switching between modes implies transfer mode

            // write model to file
            model.exportModel("trip1-5.lp");

            // solve the model and display the solution if one was found
            boolean isSolved = model.solve();
            model.output().println("Solution status = " + model.getStatus());
            List<BaseArc> result = new ArrayList<>();
            if (isSolved) {
                model.output().println("Solution value  = " + model.getObjValue());
                // an array of primal solution values for all the variables
                double[] values = model.getValues(x);

                int nvars = values.length;
                for (int j = 0; j < nvars; ++j) {
                    model.output().println("Variable " + j + ": Value = " + values[j]);
                    if (values[j] == 1) {
                        BaseArc arc = arcs.get(j);
                        result.add(arc);
                        System.err.println(arc);
                    }
                }
            }
            return result;


        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }


    private void filterInOutArcs(Map<BaseNode, Set<Integer>> groupedArcs, Set<Set<Integer>> groupedInOutArcs) {
        for (Iterator<Set<Integer>> i = groupedArcs.values().iterator(); i.hasNext(); ) {
            Set<Integer> arcs = i.next();
            if (arcs.size() <= 1) {
                i.remove();
            } else {
                groupedInOutArcs.add(arcs);
            }
        }
    }


//    private void addUniqueInOutArcsConstraint(IloCplex model, IloIntVar[] x, Set<Set<Integer>> groupedArcs) throws IloException {
//        for (Set<Integer> indexes : groupedArcs) {
//            IloNumVar[] arcsVariables = indexes.stream().map(ind -> x[ind]).toArray(IloNumVar[]::new);
//            model.addLe(model.sum(arcsVariables), 1.0);
//        }
//    }

//    private void addObjectiveFunction(IloCplex model, IloNumVar[] x, int[] visitingArcsIndexes) throws IloException {
//        IloNumVar[] visitArcsVariables = new IloNumVar[visitingArcsIndexes.length];
//        int i = 0;
//        for (int index : visitingArcsIndexes) {
//            visitArcsVariables[i] = x[index];
//            i++;
//        }
//        model.addMaximize(model.sum(visitArcsVariables));
//    }

    private void addObjectiveFunction(IloCplex model, IloNumVar[] x, int[] visitMask) throws IloException {
        model.addMaximize(model.scalProd(visitMask, x));
    }


    private static InputParameters getFakeInputParameters() {
        Long departurePoint = 1L;
        LocalTime depatureTime = TimeUtils.minutesToTime(1);

        Long arrivalPoint = 9L;
        LocalTime arrivalTime = TimeUtils.minutesToTime(30);
//        LocalTime depatureTime =  LocalTime.of(9, 0);
//        LocalTime arrivalTime = LocalTime.of(11, 0);
        Set<TransportMode> modes = EnumSet.of(TransportMode.BUS);
        Map<Long, Integer> visitPois = new HashMap<Long, Integer>() {{
            put(3L, 10);
            put(7L, 7);
            put(8L, 12);
        }};
        LinkedHashMap<RouteCriteria, Double> criteria = new LinkedHashMap<RouteCriteria, Double>() {{
            put(RouteCriteria.MAX_POI, 10.0);
        }};

        return new InputParameters(
                departurePoint,
                arrivalPoint,
                depatureTime,
                arrivalTime,
                modes,
                visitPois,
                criteria
        );
    }

    private static List<BaseArc> getNetworkArcs() {

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
        }};

        return new ArrayList<BaseArc>() {{
            add(new BusArc(
                    new BaseNode(places.get("a"), 1),
                    new BaseNode(places.get("b"), 3)
            ));
            // right branch
            add(new BusArc(
                    new BaseNode(places.get("b"), 3),
                    new BaseNode(places.get("f"), 7)
            ));
            add(new BusArc(
                    new BaseNode(places.get("f"), 7),
                    new BaseNode(places.get("g"), 10)
            ));
            add(new VisitArc(
                    new BaseNode(places.get("g"), 10),
                    new BaseNode(places.get("g"), 15)
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

            //left branch
            add(new BusArc(
                    new BaseNode(places.get("b"), 3),
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
                    new BaseNode(places.get("k"), 17)
            ));
            add(new BusArc(
                    new BaseNode(places.get("e"), 17),
                    new BaseNode(places.get("k"), 22)
            ));
        }};
    }


}
