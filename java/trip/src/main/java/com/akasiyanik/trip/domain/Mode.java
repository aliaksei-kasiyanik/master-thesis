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

    BUS,
    METRO,

    ;

    public static final EnumSet<Mode> TRANSPORT = EnumSet.of(BUS, METRO);


}
