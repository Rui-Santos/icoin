package com.icoin.trading.tradeengine.query.activity;

import com.homhon.base.domain.ValueObject;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-7
 * Time: AM12:47
 * To change this template use File | Settings | File Templates.
 */
public enum PortfolioActivityType implements ValueObject<PortfolioActivityType> {
    WITHDRAW_COIN,
    ADD_COIN,
    WITHDRAW_MONEY,
    ADD_MONEY,
    BUY_ORDER_ACTIVITY,
    SELL_ORDER_ACTIVITY,
    WITHDRAW_LARGE_AMOUNT_OF_MONEY,
    ADD_LARGE_AMOUNT_OF_MONEY,
    WITHDRAW_LARGE_AMOUNT_OF_COIN,
    ADD_LARGE_AMOUNT_OF_COIN;

    @Override
    public boolean sameValueAs(PortfolioActivityType other) {
        return other == this;
    }

    @Override
    public PortfolioActivityType copy() {
        return this;
    }
}