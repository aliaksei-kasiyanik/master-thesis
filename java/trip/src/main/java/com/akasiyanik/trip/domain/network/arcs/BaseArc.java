package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.TransportMode;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author akasiyanik
 *         5/5/17
 */
public class BaseArc {

    private final BaseNode i;

    private final BaseNode j;

    private final TransportMode mode;

    public BaseArc(BaseNode i, BaseNode j, TransportMode mode) {
        if (i.getTime() > j.getTime()) {
            throw new RuntimeException("j node time can't be after i node time");
        }
        this.i = i;
        this.j = j;
        this.mode = mode;
    }

    public BaseNode getI() {
        return i;
    }

    public BaseNode getJ() {
        return j;
    }

    public TransportMode getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseArc baseArc = (BaseArc) o;

        return new EqualsBuilder()
                .append(i, baseArc.i)
                .append(j, baseArc.j)
                .append(mode, baseArc.mode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(i)
                .append(j)
                .append(mode)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "[m=" + mode +
                ", i=" + i.getId() +
                ", a=" + i.getTime() +
                ", j=" + j.getId() +
                ", b=" + j.getTime() + "]";
    }
}


