package com.icoin.trading.fee.query.received;

import com.icoin.trading.api.fee.events.fee.received.ReceivedFeeCreatedEvent;
import com.icoin.trading.fee.query.fee.received.ReceivedFeeEntryQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:22
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedEntryListener {
    private static Logger logger = LoggerFactory.getLogger(ReceivedEntryListener.class);
    private ReceivedFeeEntryQueryRepository repository;

    @EventHandler
    public void handleCreated(ReceivedFeeCreatedEvent event) {
    }


}
