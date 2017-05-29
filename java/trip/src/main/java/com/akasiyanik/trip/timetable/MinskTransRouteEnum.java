package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.Mode;

/**
 * @author akasiyanik
 *         5/11/17
 */
public enum MinskTransRouteEnum {

    BUS_1("1", Type.BUS, Mode.BUS_1, "http://www.minsktrans.by/city/#minsk/bus;193309;15846;time"),
    BUS_25("25", Type.BUS, Mode.BUS_25, "http://www.minsktrans.by/city/#minsk/bus;198746;14490;time"),
    BUS_64("64", Type.BUS, Mode.BUS_64, "http://www.minsktrans.by/city/#minsk/bus;185800;14386;time"),
    BUS_100("100", Type.BUS, Mode.BUS_100, "http://www.minsktrans.by/city/#minsk/bus;193049;15014;time"),
    TRAM_5("5", Type.TRAM, Mode.TRAM_5, "http://www.minsktrans.by/city/#minsk/tram;190425;15183;time"),
    TRAM_6("6", Type.TRAM, Mode.TRAM_6, "http://www.minsktrans.by/city/#minsk/tram;196020;15360;time"),
    TRAM_11("11", Type.TRAM, Mode.TRAM_11, "http://www.minsktrans.by/city/#minsk/tram;196026;16191;time"),


    METRO_1_S("1", Type.METRO, Mode.METRO_1_S, null),
    METRO_1_B("1", Type.METRO, Mode.METRO_1_B, null),
    METRO_2_S("2", Type.METRO, Mode.METRO_2_S, null),
    METRO_2_B("2", Type.METRO, Mode.METRO_2_B, null),

    ;

    public enum Type {
        BUS,
        TRAM,
        METRO,
        TROLLEYBUS,
    };

    private String number;

    private Type type;

    private String parseUrl;

    private Mode mode;

    MinskTransRouteEnum(String number, Type type, Mode mode, String parseUrl) {
        this.number = number;
        this.type = type;
        this.mode = mode;
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

    public Mode getMode() {
        return mode;
    }

    public static MinskTransRouteEnum getRouteEnumByRoute(MinskTransRoute route) {
        for (MinskTransRouteEnum routeEnum : MinskTransRouteEnum.values()) {
            if (routeEnum.getNumber().equals(route.getNumber()) && routeEnum.getType().equals(route.getType())) {
                return routeEnum;
            }
        }
        throw new RuntimeException("Can't match RouteEnum");
    }
}
