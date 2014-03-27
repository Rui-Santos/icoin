package com.icoin.trading.fee.domain.cash;

import com.icoin.trading.fee.domain.address.Address;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-27
 * Time: AM1:17
 * To change this template use File | Settings | File Templates.
 */
public class CoinCash<T extends CoinCash> extends Cash<T>{
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
