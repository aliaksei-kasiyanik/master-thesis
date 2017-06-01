package com.akasiyanik.trip.service.walk;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author akasiyanik
 *         5/30/17
 */
@Component
public class OpenRouteService {

    private static final Logger logger = LoggerFactory.getLogger(OpenRouteService.class);

    private static final String API_KEY = "58d904a497c67e00015b45fc7e616a91eaaf442f671a9491c1e099db";

//    google directions api key AIzaSyDZKCYb0786rxtt8H1w8pcOrtWRsdEAHh0
//    google distance matrix api key AIzaSyDADd4q7w-2FmN7yibodYIEBp_srxFfiuw

    private static final String URI = "https://api.openrouteservice.org/directions?coordinates={lon1},{lat1}|{lon2},{lat2}&profile=foot-walking&preference=fastest&units=m&language=en&geometry=true&geometry_format=geojson&geometry_simplify=false&instructions=false&instructions_format=text&elevation=false&api_key={api_key}";

    @Autowired
    private RestTemplate restTemplate;

    public void getWalkingRoute(Pair<Double, Double> loc1, Pair<Double, Double> loc2) {
        Map<String, String> params = new HashMap<>();
        params.put("api_key", API_KEY);
        params.put("lat1", String.valueOf(loc1.getLeft()));
        params.put("lon1", String.valueOf(loc1.getRight()));
        params.put("lat2", String.valueOf(loc2.getLeft()));
        params.put("lon2", String.valueOf(loc2.getRight()));

        String result = restTemplate.getForObject(URI, String.class, params);
        logger.info(result);
    }
}
