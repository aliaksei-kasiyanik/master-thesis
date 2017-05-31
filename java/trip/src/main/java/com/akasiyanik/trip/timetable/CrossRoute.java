package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.Type;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author akasiyanik
 *         5/31/17
 */

public class CrossRoute {

    private String number;

    private Type type;

    public CrossRoute() {
    }

    public CrossRoute(String number, Type type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CrossRoute that = (CrossRoute) o;

        return new EqualsBuilder()
                .append(number, that.number)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(number)
                .append(type)
                .toHashCode();
    }
}
