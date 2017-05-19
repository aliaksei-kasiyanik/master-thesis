package com.akasiyanik.trip.timetable.network;

import com.akasiyanik.trip.domain.Mode;
import org.springframework.data.annotation.Id;

import java.time.LocalTime;

/**
 * @author akasiyanik
 *         5/19/17
 */
public class MinskTransArc {

    @Id
    private String id;

    private Long iId;

    private LocalTime iTime;

    private Long jId;

    private Long jTime;

    private Mode mode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getiId() {
        return iId;
    }

    public void setiId(Long iId) {
        this.iId = iId;
    }

    public LocalTime getiTime() {
        return iTime;
    }

    public void setiTime(LocalTime iTime) {
        this.iTime = iTime;
    }

    public Long getjId() {
        return jId;
    }

    public void setjId(Long jId) {
        this.jId = jId;
    }

    public Long getjTime() {
        return jTime;
    }

    public void setjTime(Long jTime) {
        this.jTime = jTime;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
