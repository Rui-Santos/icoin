package com.icoin.trading.users.domain.model.function;

import com.homhon.core.exception.IZookeyException;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-13
 * Time: AM8:29
 * To change this template use File | Settings | File Templates.
 */
public class UserAlreadyLogedOnException extends IZookeyException {
    public UserAlreadyLogedOnException() {
        super("Too many resets!");
    }
}
