package com.akasiyanik.trip.domain.network.arcs;

import com.akasiyanik.trip.domain.Mode;
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

    private final Mode mode;

    public BaseArc(BaseNode i, BaseNode j, Mode mode) {
        if (i.getTime() >= j.getTime()) {
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

    public Mode getMode() {
        return mode;
    }

    public int getTime() {
        return j.getTime() - i.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BaseArc)) return false;

        BaseArc arc = (BaseArc) o;

        return new EqualsBuilder()
                .append(i, arc.i)
                .append(j, arc.j)
                .append(mode, arc.mode)
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


