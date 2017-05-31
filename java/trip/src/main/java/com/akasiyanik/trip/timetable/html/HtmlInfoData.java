package com.akasiyanik.trip.timetable.html;

/**
 * @author akasiyanik
 *         5/31/17
 */
public class HtmlInfoData {

    private String timetableHtml;

    private String stopsHtml;

    public HtmlInfoData() {
    }

    public HtmlInfoData(String timetableHtml, String stopsHtml) {
        this.timetableHtml = timetableHtml;
        this.stopsHtml = stopsHtml;
    }

    public String getTimetableHtml() {
        return timetableHtml;
    }

    public void setTimetableHtml(String timetableHtml) {
        this.timetableHtml = timetableHtml;
    }

    public String getStopsHtml() {
        return stopsHtml;
    }

    public void setStopsHtml(String stopsHtml) {
        this.stopsHtml = stopsHtml;
    }
}
