package com.akasiyanik.trip;

import com.akasiyanik.trip.cplex.ProblemSolver;
import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.service.RoutePrinter;
import com.akasiyanik.trip.service.network.NetworkGenerationService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;

/**
 * @author akasiyanik
 *         5/22/17
 */
@Component
public class TripRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(TripRunner.class);

    @Autowired
    private NetworkGenerationService networkService;

    @Autowired
    private RoutePrinter printer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InputParameters parameters = getParameters();

        List<BaseArc> arcs = networkService.generateNetwork(parameters);

        ProblemSolver solver = new ProblemSolver(arcs, parameters);
        List<List<BaseArc>> result = solver.solve();

        logger.debug("{}", result);
        printer.print(result);
    }

    private InputParameters getParameters() {
        String departurePoint = "59317e620cc7842d442760af"; // uralskaya
        LocalTime departureTime =  LocalTime.of(8, 58);

        String arrivalPoint = "592ef61cb929d5fc5c451647";// Кропоткина
        LocalTime arrivalTime = LocalTime.of(11, 0);

        Set<Mode> modes = EnumSet.of(
                Mode.BUS_25_S,
                Mode.BUS_25_B
                );
        Map<String, Integer> visitPois = new HashMap<String, Integer>() {{
            put("59317e620cc7842d442760a9", 10);
//            put(7L, 7);
//            put(8L, 12);
        }};
        List<Pair<RouteCriteria, Double>> criteria = new LinkedList<Pair<RouteCriteria, Double>>() {{
//            add(new ImmutablePair<>(RouteCriteria.MIN_CO2, 0.3));
            add(new ImmutablePair<>(RouteCriteria.MAX_POI, 0.0));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME, 0.0));
//            add(new ImmutablePair<>(RouteCriteria.MIN_CHANGES, 0.1));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_WALKING, 0.0));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_TRANSFER, 0.0));
            add(new ImmutablePair<>(RouteCriteria.MIN_COST, 0.0));
        }};

        return new InputParameters(
                departurePoint,
                arrivalPoint,
                departureTime,
                arrivalTime,
                modes,
                visitPois,
                criteria
        );
    }
}
