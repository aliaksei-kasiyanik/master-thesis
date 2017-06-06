package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Type;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.service.walk.OpenRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author akasiyanik
 *         5/5/17
 */
@Service
public class NetworkGenerationService {

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

        return new ArrayList<>(allArcs);
    }

}
