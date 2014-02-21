package com.icoin.trading.webui.global;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-2-21
 * Time: AM1:42
 * To change this template use File | Settings | File Templates.
 */
@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
}
