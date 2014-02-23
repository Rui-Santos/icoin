package com.icoin.trading.webui.global;

import com.homhon.core.exception.IZookeyException;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-22
 * Time: AM1:48
 * To change this template use File | Settings | File Templates.
 */
public class AxonInternalException extends IZookeyException {
    public AxonInternalException(String message) {
        super(message);
    }
}
