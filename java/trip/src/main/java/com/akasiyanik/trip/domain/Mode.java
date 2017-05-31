package com.akasiyanik.trip.domain;

import static com.akasiyanik.trip.domain.Type.*;

/**
 * @author akasiyanik
 *         5/5/17
 */
public enum Mode {

    TRANSFER,
    VISIT,
    DUMMY_START_FINISH,

    WALK,

    METRO_1_S(METRO),
    METRO_1_B(METRO),
    METRO_2_S(METRO),
    METRO_2_B(METRO),

    BUS_1_S(BUS),
    BUS_1_B(BUS),
    BUS_19_S(BUS),
    BUS_19_B(BUS),
    BUS_25_S(BUS),
    BUS_25_B(BUS),
    BUS_64_S(BUS),
    BUS_64_B(BUS),
    BUS_100_S(BUS),
    BUS_100_B(BUS),

    TRAM_5_S(TRAM),
    TRAM_5_B(TRAM),
    TRAM_6_S(TRAM),
    TRAM_6_B(TRAM),
    TRAM_11_S(TRAM),
    TRAM_11_B(TRAM),
    ;

    private Type type;

    Mode() {
        this.type = FAKE;
    }

    Mode(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public int getCost() {
        return type.getCost();
    }

    public int getCo2() {
        return type.getCo2();
    }

    public boolean isTransport() {
        return !this.type.equals(FAKE);
    }

    public boolean isCO2Transport() {
        return this.type.equals(BUS);
    }


}
