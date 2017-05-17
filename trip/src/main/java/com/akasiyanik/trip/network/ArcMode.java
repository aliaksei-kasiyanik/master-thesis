package com.akasiyanik.trip.network;

/**
 * @author akasiyanik
 *         3/27/17
 */
public enum ArcMode {

    PUBLIC(0),
    TIMETABLE_FREE(1),
    BICYCLES(2),
    WALKING(3),

    TRANSFER(4), // for waiting in some point (transport mode change, POI opening)
    VISIT(5),
    ;

    private int index;

    ArcMode(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
