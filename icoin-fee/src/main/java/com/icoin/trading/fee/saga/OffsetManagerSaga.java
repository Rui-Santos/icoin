package com.icoin.trading.fee.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM7:16
 * To change this template use File | Settings | File Templates.
 */
public class OffsetManagerSaga extends AbstractAnnotatedSaga {
    private transient CommandGateway commandGateway;
}
