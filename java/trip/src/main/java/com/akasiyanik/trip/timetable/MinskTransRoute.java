package com.akasiyanik.trip.timetable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.util.List;

/**
 * @author akasiyanik
 *         5/10/17
 */
@CompoundIndexes({
        @CompoundIndex(name = "number1_rev1", def = "{'number' : 1, 'reverse': 1}")
})
public class MinskTransRoute {

    @Id
    private String id;

    private String number;

    private String name;

    private boolean reverse = false;

    private List<Long> stopIds;

    private List<List<String>> threads;

    public MinskTransRoute(String number, boolean reverse) {
        this.number = number;
        this.reverse = reverse;
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

    public List<List<String>> getThreads() {
        return threads;
    }

    public void setThreads(List<List<String>> threads) {
        this.threads = threads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MinskTransRoute that = (MinskTransRoute) o;

        return new EqualsBuilder()
                .append(reverse, that.reverse)
                .append(number, that.number)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(number)
                .append(reverse)
                .toHashCode();
    }
}
