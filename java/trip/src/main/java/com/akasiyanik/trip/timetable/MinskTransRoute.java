package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * @author akasiyanik
 *         5/10/17
 */
public class MinskTransRoute {

    @Id
    private String id;

    private Mode mode;

    private Type type;

    private String number;

    private String name;

    private boolean reverse = false;

    private List<Long> stopIds;

    private List<List<Integer>> threads;

    public MinskTransRoute() {
    }

    public MinskTransRoute(String number, boolean reverse, Type type, Mode mode) {
        this.number = number;
        this.reverse = reverse;
        this.type = type;
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public List<Long> getStopIds() {
        return stopIds;
    }

    public void setStopIds(List<Long> stopIds) {
        this.stopIds = stopIds;
    }

    public List<List<Integer>> getThreads() {
        return threads;
    }

    public void setThreads(List<List<Integer>> threads) {
        this.threads = threads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MinskTransRoute that = (MinskTransRoute) o;

        return new EqualsBuilder()
                .append(reverse, that.reverse)
                .append(type, that.type)
                .append(number, that.number)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(number)
                .append(reverse)
                .toHashCode();
    }
}
