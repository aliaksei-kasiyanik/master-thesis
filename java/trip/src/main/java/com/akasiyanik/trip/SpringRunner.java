package com.akasiyanik.trip;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.RouteCriteria;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
public class SpringRunner implements ApplicationRunner {

    @Autowired
    private MinskTransTripRunner tripRunner;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InputParameters parameters = getParameters();
        tripRunner.run(parameters);
    }

    private InputParameters getParameters() {
        String departurePoint = "592ece9db929d5e7b04ff9bd"; // uralskaya
        LocalTime departureTime =  LocalTime.of(8, 58);

        String arrivalPoint = "592ef61cb929d5fc5c451647";// ds urucie 2
        LocalTime arrivalTime = LocalTime.of(11, 0);

        Set<Mode> modes = EnumSet.of(
                Mode.BUS_25_S,
                Mode.BUS_25_B
                );
        Map<Long, Integer> visitPois = new HashMap<Long, Integer>() {{
//            put(3L, 10);
//            put(7L, 7);
//            put(8L, 12);
        }};
        List<Pair<RouteCriteria, Double>> criteria = new LinkedList<Pair<RouteCriteria, Double>>() {{
//            add(new ImmutablePair<>(RouteCriteria.MIN_COST, 0.3));
//            add(new ImmutablePair<>(RouteCriteria.MIN_CO2, 0.3));
//            add(new ImmutablePair<>(RouteCriteria.MAX_POI, 0.1));
            add(new ImmutablePair<>(RouteCriteria.MIN_TIME, 0.2));
//            add(new ImmutablePair<>(RouteCriteria.MIN_CHANGES, 0.1));
//            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_TRANSFER, 0.1));
//            add(new ImmutablePair<>(RouteCriteria.MIN_TIME_WALKING, 0.1));
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
