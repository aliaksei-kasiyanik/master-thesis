package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;

import java.time.LocalTime;
import java.util.List;

/**
 * @author akasiyanik
 *         5/10/17
 */
public interface NetworkGenerator<T> {

    List<T> generateArcs(InputParameters parameters);

}
