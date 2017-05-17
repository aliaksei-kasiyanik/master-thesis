package com.akasiyanik.trip.network;

import java.util.Date;

/**
 * @author akasiyanik
 *         3/27/17
 */
public class Node {

    private Double lat;

    private Double lon;

    private Date time; // departure or arrival

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
