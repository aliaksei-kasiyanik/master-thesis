package com.akasiyanik.trip.domain;

/**
 * @author akasiyanik
 *         5/30/17
 */
public enum Type {

    FAKE,

    BUS(55, 8), // 0.8 mg/km
    TRAM(55, 0),
    METRO(60, 0),
    TROLLEYBUS(55, 0),
    ;

    private int cost;

    private int co2;

    Type() {
        this.cost = 0;
        this.co2 = 0;
    }

    Type(int cost, int co2) {
        this.cost = cost;
        this.co2 = co2;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }
};