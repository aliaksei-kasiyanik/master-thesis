package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Repository
public interface TimetableRepository {

    List<MinskTransRoute> getByEnum(MinskTransRouteEnum routeEnum);

    void save(MinskTransRouteEnum routeEnum, List<MinskTransRoute> routes);

    boolean exist(MinskTransRouteEnum routeEnum);

}
