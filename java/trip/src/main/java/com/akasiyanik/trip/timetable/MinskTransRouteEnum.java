package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.Type;

import java.util.Arrays;
import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
public enum MinskTransRouteEnum {

    BUS_1("1", Type.BUS, Arrays.asList(Mode.BUS_1_S, Mode.BUS_1_B), "http://www.minsktrans.by/city/#minsk/bus;193309;15846;time"),
    BUS_19("19", Type.BUS, Arrays.asList(Mode.BUS_19_S, Mode.BUS_19_B), "http://www.minsktrans.by/city/#minsk/bus;180419;15750;time"),
    BUS_25("25", Type.BUS, Arrays.asList(Mode.BUS_25_S, Mode.BUS_25_B), "http://www.minsktrans.by/city/#minsk/bus;198746;14490;time"),
    BUS_64("64", Type.BUS, Arrays.asList(Mode.BUS_64_S, Mode.BUS_64_B), "http://www.minsktrans.by/city/#minsk/bus;185800;14386;time"),
    BUS_100("100", Type.BUS, Arrays.asList(Mode.BUS_100_S, Mode.BUS_100_B), "http://www.minsktrans.by/city/#minsk/bus;193049;15014;time"),
    TRAM_5("5", Type.TRAM, Arrays.asList(Mode.TRAM_5_S, Mode.TRAM_5_B), "http://www.minsktrans.by/city/#minsk/tram;196010;15183;time"),
    TRAM_6("6", Type.TRAM, Arrays.asList(Mode.TRAM_6_S, Mode.TRAM_6_B), "http://www.minsktrans.by/city/#minsk/tram;196020;15360;time"),
    TRAM_11("11", Type.TRAM, Arrays.asList(Mode.TRAM_11_S, Mode.TRAM_11_B), "http://www.minsktrans.by/city/#minsk/tram;196026;16191;time"),

    TROL_22("22", Type.TROLLEYBUS, Arrays.asList(Mode.TROL_22_S, Mode.TROL_22_B), "http://www.minsktrans.by/city/#minsk/trol;189232;15734;time"),

    METRO_1("1", Type.METRO, Arrays.asList(Mode.METRO_1_S, Mode.METRO_1_B), null),
    METRO_2("2", Type.METRO, Arrays.asList(Mode.METRO_2_S, Mode.METRO_2_B), null),

    ;

    private String number;

    private Type type;

    private String parseUrl;

    private List<Mode> modes;

    MinskTransRouteEnum(String number, Type type, List<Mode> modes, String parseUrl) {
        this.number = number;
        this.type = type;
        this.parseUrl = parseUrl;
        this.modes = modes;
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

    public List<Mode> getModes() {
        return modes;
    }
}
