package com.akasiyanik.trip;

import com.akasiyanik.trip.cplex.ProblemSolver;
import com.akasiyanik.trip.cplex.solution.RouteSolution;
import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.Type;
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
        double epsilon = 0.0;
        InputParameters parameters = getParameters(epsilon);

        List<BaseArc> arcs = networkService.generateNetwork(parameters);

        ProblemSolver solver = new ProblemSolver(arcs, parameters);
        RouteSolution result = solver.solve();

        logger.debug("{}", result);
        printer.print(result);
    }

    private InputParameters getParameters(double epsilon) {
        String departurePoint = "5939349989d07f2674224773"; // ДС Зелёный Луг
        LocalTime departureTime =  LocalTime.of(8, 58);

        String arrivalPoint = "59317e620cc7842d442760ad";// Partyzanskaya
        LocalTime arrivalTime = LocalTime.of(11, 0);

        Set<Type> modes = EnumSet.of(
                Type.WALK,
                Type.BUS,
                Type.METRO,
                Type.TRAM,
                Type.TROLLEYBUS
                );
        Map<String, Integer> visitPois = new HashMap<String, Integer>() {{
            put("59317e620cc7842d442760a9", 10);
            put("5939349e89d07f2674224778", 10);
        }};
        List<Pair<RouteCriteria, Double>> criteria = new LinkedList<Pair<RouteCriteria, Double>>() {{
            add(new ImmutablePair<>(RouteCriteria.MAX_POI, epsilon));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME, epsilon));
            add(new ImmutablePair<>(RouteCriteria.MIN_CHANGES, epsilon));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_TRANSFER, epsilon));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_WALKING, epsilon));
            add(new ImmutablePair<>(RouteCriteria.MIN_COST, epsilon));
            add(new ImmutablePair<>(RouteCriteria.MIN_CO2, epsilon));
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
