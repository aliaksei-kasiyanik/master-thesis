package com.akasiyanik.trip.service.walk;

import com.akasiyanik.trip.timetable.TransportStop;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author akasiyanik
 *         6/1/17
 */
@Service
public class GoogleDistanceService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDistanceService.class);

    private static final String API_KEY = "AIzaSyDADd4q7w-2FmN7yibodYIEBp_srxFfiuw";

    private static final String URI = "https://maps.googleapis.com/maps/api/distancematrix/json?origins={origins}&destinations={destinations}&mode={mode}&key={key}";

    private JsonParser parser = new JsonParser();

    @Autowired
    private RestTemplate restTemplate;

    public WalkDistance getWalkingDistance(TransportStop stop1, TransportStop stop2) {
        Map<String, String> params = new HashMap<>();
        params.put("origins", stop1.getLocation().getLon() + "," + stop1.getLocation().getLat());
        params.put("destinations", stop2.getLocation().getLon() + "," + stop2.getLocation().getLat());
        params.put("mode", "walking");
        params.put("language", "en");
        params.put("key", API_KEY);


        String result = restTemplate.getForObject(URI, String.class, params);
        JsonObject jsonObject = parser.parse(result).getAsJsonObject();
        WalkDistance walkDistance = null;
        if (jsonObject.get("status").getAsString().equals("OK")) {
            JsonObject element = jsonObject.getAsJsonArray("rows").get(0).getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();

            logger.info(result);

            if (element.get("status").getAsString().equals("OK")) {
                Long minutes = element.getAsJsonObject("duration").get("value").getAsLong() / 60;
                if (minutes == 0) {
                    minutes = 1L;
                }
                Long meters = element.getAsJsonObject("distance").get("value").getAsLong();

                walkDistance = new WalkDistance();
                walkDistance.setNodesIds(Arrays.asList(stop1.getId(), stop2.getId()));
                walkDistance.setMinutes(minutes);
                walkDistance.setMeters(meters);
            }
        }
        return walkDistance;
    }

}
