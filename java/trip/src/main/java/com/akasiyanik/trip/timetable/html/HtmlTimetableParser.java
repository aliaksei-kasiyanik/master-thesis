package com.akasiyanik.trip.timetable.html;

import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.utils.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Service
public class HtmlTimetableParser {

    public List<MinskTransRoute> parseFromFile(String filename) {
        String html = IOUtils.readFileAsString(filename);
        return parseFromString(html);
    }

    public List<MinskTransRoute> parseFromString(String html) {
        Document doc = Jsoup.parse(html);

        String routeNumber = doc.select("div div div b").first().html();
        return parseRoutes(doc, routeNumber);
    }

    private List<MinskTransRoute> parseRoutes(Document doc, String routeNumber) {

//        Direct Route (A -> B)

        String routeNameAB = doc.select("div div div span").get(0).html();
        MinskTransRoute routeAB = new MinskTransRoute(routeNumber, false);
        routeAB.setName(routeNameAB);

        Element ABElements = doc.select("div div div table tbody").get(0);
        List<List<String>> ABThreads = getThreads(ABElements);
        List<Long>  ABStopsIds = getStops(ABElements);
        routeAB.setStopIds(ABStopsIds);
        routeAB.setThreads(ABThreads);

//        Reverse Route (B -> A)

        String routeNameBA = doc.select("div div div span").get(1).html();
        MinskTransRoute routeBA = new MinskTransRoute(routeNumber, true);
        routeBA.setName(routeNameBA);

        Element BAElements = doc.select("div div div table tbody").get(1);
        List<List<String>> BAThreads = getThreads(BAElements);
        List<Long> BAStopsIds = getStops(BAElements);
        routeBA.setStopIds(BAStopsIds);
        routeBA.setThreads(BAThreads);


        return Arrays.asList(routeAB, routeBA);
    }

    private List<Long> getStops(Element routeElements) {
        Elements stopElements = routeElements.select("tr:gt(0) td:eq(1) a");
//        Map<String, String> stops = new HashMap<>(stopElements.size());
        List<Long> ids = new ArrayList<>();
        for (Element e : stopElements) {
            String id = e.attr("href").split(";")[2];
//            String name = e.html();
            ids.add(Long.valueOf(id.trim()));
        }
        return ids;
    }

    private List<List<String>> getThreads(Element routeElements) {
        Integer threadsCount = Integer.valueOf(routeElements.select("tr:eq(0) td:eq(2)").attr("colspan"));
        List<List<String>> threads = new ArrayList<>(threadsCount);

        for (int threadIndex = 0; threadIndex < threadsCount; threadIndex++) {
            int trIndex = threadIndex + 2;
            Elements oneThreadElements = routeElements.select("tr:gt(0) td:eq(" + trIndex + ")");
            List<String> thread = new ArrayList<>(oneThreadElements.size());
            oneThreadElements.forEach(time -> thread.add(time.html().split("&")[0]));
            threads.add(thread);
        }
        return threads;
    }


}
