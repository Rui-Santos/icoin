package com.icoin.trading.tradeengine.application;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-23
 * Time: PM11:41
 * To change this template use File | Settings | File Templates.
 */
public interface Callback<V> {
    String getIdentifier();

    V execute() throws Exception;
}
