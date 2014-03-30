package com.icoin.trading.bitcoin.client;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-24
 * Time: PM9:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class ValueObject<T extends ValueObject> implements Serializable {

    private transient int cachedHashCode = 0;

    /**
     * @param other The other auditCollection object.
     * @return True if all non-transient fields are equal.
     */
    public final boolean sameValueAs(final T other) {
        return other != null && EqualsBuilder.reflectionEquals(this, other, false);
    }

    public T copy() {
        return (T) ObjectUtils.clone(this);
    }

    /**
     * @return Hash code built from all non-transient fields.
     */
    @Override
    public int hashCode() {
        // Using a local variable to ensure that we only do a single read
        // of the cachedHashCode field, to avoid race conditions.
        // It doesn't matter if several threads compute the hash code and overwrite
        // each other, but it's important that we never return 0, which could happen
        // with multiple reads of the cachedHashCode field.
        //
        // See java.lang.String.hashCode()
        int h = cachedHashCode;
        if (h == 0) {
            // Lazy initialization of hash code.
            // Value objects are immutable, so the hash code never changes.
            h = HashCodeBuilder.reflectionHashCode(this, false);
            cachedHashCode = h;
        }

        return h;
    }

    /**
     * @param o other object
     * @return True if other object has the same auditCollection as this auditCollection object.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return sameValueAs((T) o);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}