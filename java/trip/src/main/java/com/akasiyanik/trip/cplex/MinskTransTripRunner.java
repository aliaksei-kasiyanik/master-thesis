package com.akasiyanik.trip.cplex;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.service.network.NetworkGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author akasiyanik
 *         5/22/17
 */
@Component
public class MinskTransTripRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProblemSolver.class);

    @Autowired
    private NetworkGenerationService networkService;

    public void run(InputParameters parameters) {
        List<BaseArc> arcs = networkService.generateNetwork(parameters);

        ProblemSolver solver = new ProblemSolver(arcs, parameters);
        List<BaseArc> result = solver.solve();

        logger.info("{}", result);
    }

}
