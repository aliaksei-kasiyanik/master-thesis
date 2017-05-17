package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.utils.IOUtils;
import com.akasiyanik.trip.utils.GsonSerializer;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Repository
public class MinskTransTimetableRepository implements TimetableRepository {

    @Override
    public List<MinskTransRoute> getByEnum(MinskTransRouteEnum routeEnum) {
        if (exist(routeEnum)) {
            String json = IOUtils.readFileAsString(getFilePath(routeEnum));
            return GsonSerializer.deserialize(json, MinskTransRoute.class);
        } else {
            return null;
        }
    }

    @Override
    public void save(MinskTransRouteEnum routeEnum, List<MinskTransRoute> routes) {
        String json = GsonSerializer.serialize(routes);
        IOUtils.writeToFile(getFilePath(routeEnum), json);
    }

    @Override
    public boolean exist(MinskTransRouteEnum routeEnum) {
        return Files.exists(Paths.get(getFilePath(routeEnum)));
    }

    private String getFilePath(MinskTransRouteEnum routeEnum) {
        return "routes/" + routeEnum.getNumber() + ".json";
    }
}
