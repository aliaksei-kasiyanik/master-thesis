package com.akasiyanik.trip.network;

/**
 * @author akasiyanik
 *         3/27/17
 */
public class Arc {

    private Node a;
    private Node b;

    private ArcMode mode;

    // OPTIONAL

    private Double distance; // required

    private Double speed; // average for public transport or recommended for timetable-free

    private Double cost;

    private Double co2; // zero for tranfer, biking, walking modes

}
