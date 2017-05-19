package com.akasiyanik.trip.timetable;

/**
 * @author akasiyanik
 *         5/11/17
 */
public enum MinskTransRouteEnum {

    BUS_1("1", Type.BUS, "http://www.minsktrans.by/city/#minsk/bus;193309;15846;time"),
    BUS_64("64", Type.BUS, "http://www.minsktrans.by/city/#minsk/bus;185800;14386;time"),
    TRAM_6("6", Type.TRAM, "http://www.minsktrans.by/city/#minsk/tram;196020;15360;time"),
    ;

    public enum Type {
        BUS,
        TRAM,
        TROLLEYBUS,
    };

    private String number;

    private Type type;

    private String parseUrl;

    MinskTransRouteEnum(String number, Type type, String parseUrl) {
        this.number = number;
        this.type = type;
        this.parseUrl = parseUrl;
    }

    public String getNumber() {
        return number;
    }

    public Type getType() {
        return type;
    }

    public String getParseUrl() {
        return parseUrl;
    }
}
