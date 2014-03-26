package com.icoin.trading.fee.domain.coin;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM11:36
 * To change this template use File | Settings | File Templates.
 */
public enum CoinStatus implements ValueObject<CoinStatus> {
    CREATED,
    COMPLETE,
    CANCELLED;

    @Override
    public boolean sameValueAs(CoinStatus other) {
        return other == this;
    }

    @Override
    public CoinStatus copy() {
        return this;
    }
}
