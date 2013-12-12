package com.icoin.trading.tradeengine.domain.model.commission;

import com.homhon.core.exception.IZookeyException;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-9-26
 * Time: PM10:08
 * To change this template use File | Settings | File Templates.
 */
public class CommissionGreaterThanProductPriceException extends IZookeyException {
    public CommissionGreaterThanProductPriceException(String msg) {
        super(msg);
    }
}
