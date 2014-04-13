package com.icoin.trading.fee.domain.cash;

import com.icoin.trading.fee.domain.address.Address;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: PM9:34
 * To change this template use File | Settings | File Templates.
 */
public class CoinReceiveCash extends ReceiveCash<CoinReceiveCash> {
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
