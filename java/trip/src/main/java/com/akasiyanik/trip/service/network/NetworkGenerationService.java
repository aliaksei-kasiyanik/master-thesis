package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.service.walk.OpenRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akasiyanik
 *         5/5/17
 */
@Service
public class NetworkGenerationService {

    @Autowired
    private MinskTransNetworkGenerator minskTransNetworkGenerator;

    @Autowired
    private MetroNetworkGenerator metroNetworkGenerator;

    @Autowired
    private DummyArcsGenerator dummyArcsGenerator;

    @Autowired
    private WalkingArcsGenerator walkingArcsGenerator;



    public List<BaseArc> generateNetwork(InputParameters parameters) {

//        openRouteServiceClient.getWalkingRoute(new ImmutablePair<>(53.93286, 27.691619), new ImmutablePair<>(53.926644, 27.682736));

        List<BaseArc> allArcs = new ArrayList<>();

//        allArcs.addAll(metroNetworkGenerator.generateArcs(parameters));
        List<BaseArc> transportArcs = minskTransNetworkGenerator.generateArcs(parameters);
        allArcs.addAll(transportArcs);
        allArcs.addAll(walkingArcsGenerator.generateArcs(parameters, transportArcs));
        allArcs.addAll(dummyArcsGenerator.generateArcs(parameters, allArcs));

        return allArcs;
    }

}
