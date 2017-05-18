package com.akasiyanik.trip.domain;

import java.util.EnumSet;

/**
 * @author akasiyanik
 *         5/5/17
 */
public enum Mode {

    TRANSFER,
    VISIT,
    DUMMY_START_FINISH,

    WALK,

    BUS(55),
    METRO(60),
    ;

    public static final EnumSet<Mode> TRANSPORT = EnumSet.of(BUS, METRO);

    private int cost;

    Mode() {
        this.cost = 0;
    }

    Mode(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public boolean isTransport() {
        return TRANSPORT.contains(this);
    }


}
