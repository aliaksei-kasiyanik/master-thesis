package com.akasiyanik.trip.timetable.html;

import com.akasiyanik.trip.domain.Type;
import com.akasiyanik.trip.timetable.*;
import com.akasiyanik.trip.timetable.repository.MongoMinskTransStopRepository;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.akasiyanik.trip.utils.TimeUtils.timeToMinutes;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Service
public class HtmlTimetableParser {

    @Autowired
    private MongoMinskTransStopRepository minskTransStopRepository;

    @Autowired
    private MongoStopRepository stopRepository;

    public List<MinskTransRoute> parseFromString(HtmlInfoData html, MinskTransRouteEnum routeEnum) {
        Document timetableDoc = Jsoup.parse(html.getTimetableHtml());
        MinskTransRoute routeS = parseTimetableS(timetableDoc, routeEnum);
        MinskTransRoute routeB = parseTimetableB(timetableDoc, routeEnum);

        Document stopsDoc = Jsoup.parse(html.getStopsHtml());
        List<TransportStop> stopsS = parseStopsS(stopsDoc);
        routeS.setStopIds(getStopIds(stopsS));

        List<TransportStop> stopsB = parseStopsB(stopsDoc);
        routeB.setStopIds(getStopIds(stopsB));

        return Arrays.asList(routeS, routeB);
    }

    private List<String> getStopIds(List<TransportStop> stopsFromHtml) {
        List<String> stopIds = new ArrayList<>();
        for (TransportStop stop : stopsFromHtml) {
            List<TransportStop> existStops = stopRepository.findByName(stop.getName());
            String stopId = null;
            if (CollectionUtils.isNotEmpty(existStops)) {
                Optional<TransportStop> existStop = existStops.stream().filter(es -> es.getCrossRoutes().equals(stop.getCrossRoutes())).findFirst();
                if (existStop.isPresent()) {
                    stopId = existStop.get().getId();
                }
            }
            if (stopId == null) {
                stopRepository.save(stop);
                stopId = stop.getId();
            }
            stopIds.add(stopId);
        }
        return stopIds;
    }

    private List<TransportStop> parseStopsS(Document doc) {
        Element ABElements = doc.select("div div div table tbody").get(0);
        return getStops(ABElements);
    }

    private List<TransportStop> parseStopsB(Document doc) {
        Element BAElements = doc.select("div div div table tbody").get(1);
        return getStops(BAElements);
    }

    private MinskTransRoute parseTimetableS(Document doc, MinskTransRouteEnum routeEnum) {

        String routeNumber = doc.select("div div div b").first().html();

//        Direct Route (A -> B)

        String routeNameAB = doc.select("div div div span").get(0).html();
        MinskTransRoute routeAB = new MinskTransRoute(routeNumber, false, routeEnum.getType(), routeEnum.getModes().get(0));
        routeAB.setName(routeNameAB);

        Element ABElements = doc.select("div div div table tbody").get(0);
        List<List<Integer>> ABThreads = getThreads(ABElements);
        routeAB.setThreads(ABThreads);

        return routeAB;
    }

    private MinskTransRoute parseTimetableB(Document doc, MinskTransRouteEnum routeEnum) {

        String routeNumber = doc.select("div div div b").first().html();

//        Reverse Route (B -> A)

        String routeNameBA = doc.select("div div div span").get(1).html();
        MinskTransRoute routeBA = new MinskTransRoute(routeNumber, true, routeEnum.getType(), routeEnum.getModes().get(1));
        routeBA.setName(routeNameBA);

        Element BAElements = doc.select("div div div table tbody").get(1);
        List<List<Integer>> BAThreads = getThreads(BAElements);
        routeBA.setThreads(BAThreads);

        return routeBA;
    }

    private List<TransportStop> getStops(Element routeElements) {
        Elements stopElements = routeElements.select("tr td:eq(1) a");
        Elements crossRoutesElements = routeElements.select("tr td:eq(2) div");
//        Map<String, String> stops = new HashMap<>(stopElements.size());
        List<TransportStop> stops = new ArrayList<>();
        for (int i = 0; i < stopElements.size(); i++) {
            TransportStop stop = new TransportStop();

            String idString = stopElements.get(i).attr("href").split(";")[2];
            String name = stopElements.get(i).text();
            Long minskTransStopId = Long.valueOf(idString.trim());

            MinskTransStop minskTransStop = minskTransStopRepository.getStopByMinorIdAndName(minskTransStopId, name);
            int stopInd = 0;
            for (Long mtStopId : minskTransStop.getIds()) {
                if (mtStopId.equals(minskTransStopId)) {
                    stop.setLocation(minskTransStop.getLocations().get(stopInd));
                    break;
                }
                stopInd++;
            }
            stop.setName(minskTransStop.getName());

            Set<CrossRoute> crossRoutes = new HashSet<>();
            Elements crossNumbersElements = crossRoutesElements.get(i).select("a");
            for (Element crossNumberElement : crossNumbersElements) {
                Type type = getTypeByHref(crossNumberElement);
                String number = crossNumberElement.text();
                crossRoutes.add(new CrossRoute(number, type));
            }
            stop.setCrossRoutes(crossRoutes);
            stops.add(stop);
        }
        return stops;
    }

    private List<List<Integer>> getThreads(Element routeElements) {
        Integer threadsCount = Integer.valueOf(routeElements.select("tr:eq(0) td:eq(2)").attr("colspan"));
        List<List<Integer>> threads = new ArrayList<>(threadsCount);

        for (int threadIndex = 0; threadIndex < threadsCount; threadIndex++) {
            int trIndex = threadIndex + 2;
            Elements oneThreadElements = routeElements.select("tr:gt(0) td:eq(" + trIndex + ")");
            List<Integer> thread = new ArrayList<>(oneThreadElements.size());
            oneThreadElements.forEach(time -> thread.add(timeToMinutes(time.html().split("&")[0])));
            threads.add(thread);
        }
        return threads;
    }

    private Type getTypeByHref(Element e) {
        String stringType = e.attr("href").split(";")[0].split("/")[1];
        switch (stringType) {
            case "bus" : return Type.BUS;
            case "trol" : return Type.TROLLEYBUS;
            case "tram" : return Type.TRAM;
        }
        throw new RuntimeException();
    }

}
