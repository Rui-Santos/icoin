package com.icoin.trading.users.domain.model.function;

import com.homhon.core.exception.IZookeyException;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-12
 * Time: PM11:15
 * To change this template use File | Settings | File Templates.
 */
public class TooManyResetsException extends IZookeyException {
    private String operatingIp;

    public TooManyResetsException(String operatingIp) {
        super(String.format("Too many resets with ip %s", operatingIp));
        this.operatingIp = operatingIp;
    }

    public String getOperatingIp() {
        return operatingIp;
    }
}