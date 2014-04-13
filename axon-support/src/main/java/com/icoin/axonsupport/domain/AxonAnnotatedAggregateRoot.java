package com.icoin.axonsupport.domain;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-17
 * Time: PM9:08
 * To change this template use File | Settings | File Templates.
 */

import com.homhon.base.domain.Entity;
import com.homhon.base.domain.utils.Domains;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;

import java.lang.reflect.Field;

import static com.homhon.base.domain.utils.Domains.identityFieldDetermination;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/17/14
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AxonAnnotatedAggregateRoot<T extends AxonAnnotatedAggregateRoot, ID> extends AbstractAnnotatedAggregateRoot<ID> implements Entity<T, ID> {

    private Field identityField;

    @Override
    public final boolean sameIdentityAs(final T other) {
        return Domains.sameIdentityAs(this, other);
    }

    @Override
    public final ID identity() {
        if (identityField == null) {
            identityField = identityFieldDetermination(this.getClass());
        }

        try {
            return (ID) identityField.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ID getIdentifier() {
        return identity();
    }


    @Override
    public final int hashCode() {
        ID identity = identity();
        return identity == null ? 0 : identity.hashCode();
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return sameIdentityAs((T) o);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + identity() +
                '}';
    }

    @Override
    public String describe() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}