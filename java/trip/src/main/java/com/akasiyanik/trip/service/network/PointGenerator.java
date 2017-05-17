package com.akasiyanik.trip.service.network;

import java.util.Set;

/**
 * @author akasiyanik
 *         5/10/17
 */
public interface PointGenerator<T> {

    Set<T> generatePoints();

}
