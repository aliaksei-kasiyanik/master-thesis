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

    BUS(55, 8), // 0.8 mg/km
    TROLLEYBUS(55, 0),
    TRAM(55, 0),
    METRO(60, 0),
    METRO_1_S(60, 0),
    METRO_1_B(60, 0),
    METRO_2_S(60, 0),
    METRO_2_B(60, 0),

    BUS_1(55, 8),
    BUS_25(55, 8),
    BUS_64(55, 8),
    BUS_100(55, 8),

    TRAM_5(55, 0),
    TRAM_6(55, 0),
    TRAM_11(55, 0),
    ;

    public static final EnumSet<Mode> TRANSPORT = EnumSet.of(BUS, METRO, METRO_1_S, METRO_1_B, METRO_2_S, METRO_2_B, TROLLEYBUS, TRAM,
            BUS_1, BUS_25, BUS_64, BUS_100, TRAM_5, TRAM_6, TRAM_11);
    public static final EnumSet<Mode> CO2_TRANSPORT = EnumSet.of(BUS);

    private int cost;

    private int co2;

    Mode() {
        this.cost = 0;
        this.co2 = 0;
    }

    Mode(int cost, int co2) {
        this.cost = cost;
        this.co2 = co2;
    }

    public int getCost() {
        return cost;
    }

    public int getCo2() {
        return co2;
    }

    public boolean isTransport() {
        return TRANSPORT.contains(this);
    }

    public boolean isCO2Transport() {
        return CO2_TRANSPORT.contains(this);
    }


}
