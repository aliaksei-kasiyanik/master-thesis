package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.service.walk.OpenRouteServiceClient;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
    private OpenRouteServiceClient openRouteServiceClient;


    public List<BaseArc> generateNetwork(InputParameters parameters) {

//        openRouteServiceClient.getWalkingRoute(new ImmutablePair<>(53.93286, 27.691619), new ImmutablePair<>(53.926644, 27.682736));

        List<BaseArc> allArcs = new ArrayList<>();

//        allArcs.addAll(metroNetworkGenerator.generateArcs(parameters));
        allArcs.addAll(minskTransNetworkGenerator.generateArcs(parameters));
        allArcs.addAll(dummyArcsGenerator.generateArcs(parameters, allArcs));

        return allArcs;
    }

}
