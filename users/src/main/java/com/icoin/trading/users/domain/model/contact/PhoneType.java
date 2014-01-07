package com.icoin.trading.users.domain.model.contact;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-6-24
 * Time: PM8:49
 * To change this template use File | Settings | File Templates.
 */
public enum PhoneType implements ValueObject<PhoneType> {
    CELL,
    LANDLINE;

    @Override
    public boolean sameValueAs(PhoneType other) {
        return this == other;
    }

    @Override
    public PhoneType copy() {
        return this;
    }
}