package com.akasiyanik.trip.cplex;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.utils.TimeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;

/**
 * @author akasiyanik
 *         5/5/17
 */
@Component
public class TripRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProblemSolver.class);

    public static void main(String[] args) {

        InputParameters parameters = getFakeInputParameters();
        List<BaseArc> arcs = new CplexNetworkBuilder(parameters).build();

        ProblemSolver solver = new ProblemSolver(arcs, parameters);
        List<BaseArc> result = solver.solve();

        logger.info("{}", result);
    }

    private static InputParameters getFakeInputParameters() {
        String departurePoint = "a";
        LocalTime depatureTime = TimeUtils.minutesToTime(1);

        String arrivalPoint = "k";
        LocalTime arrivalTime = TimeUtils.minutesToTime(50);
//        LocalTime depatureTime =  LocalTime.of(9, 0);
//        LocalTime arrivalTime = LocalTime.of(11, 0);
        Set<Mode> modes = EnumSet.of(Mode.BUS_1_B);
        Map<Long, Integer> visitPois = new HashMap<Long, Integer>() {{
            put(3L, 10);
            put(7L, 7);
            put(8L, 12);
        }};
        List<Pair<RouteCriteria, Double>> criteria = new LinkedList<Pair<RouteCriteria, Double>>() {{
//            add(new ImmutablePair<>(RouteCriteria.MIN_COST, 0.3));
//            add(new ImmutablePair<>(RouteCriteria.MIN_CO2, 0.3));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME, 0.2));
//            add(new ImmutablePair<>(RouteCriteria.MAX_POI, 0.1));
//            add(new ImmutablePair<>(RouteCriteria.MIN_CHANGES, 0.1));
//            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_TRANSFER, 0.1));
//            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_WALKING, 0.1));
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

}
