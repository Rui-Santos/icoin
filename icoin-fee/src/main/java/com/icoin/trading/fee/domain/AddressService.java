package com.icoin.trading.fee.domain;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-26
 * Time: PM9:31
 * To change this template use File | Settings | File Templates.
 */
public interface AddressService {
    boolean validate(String address);

    String generate(String account);
}
