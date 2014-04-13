package com.icoin.trading.fee.query.fee.paid;

import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.paid.PaidFeeOffsetedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-13
 * Time: PM5:29
 * To change this template use File | Settings | File Templates.
 */
public class PaidFeeEntryListener {
    private static Logger logger = LoggerFactory.getLogger(PaidFeeEntryListener.class);
    private PaidFeeEntryQueryRepository repository;

    @EventHandler
    public void handleCreated(PaidFeeCreatedEvent event) {
        PaidFeeEntry entry = new PaidFeeEntry();
        entry.copy(event);
        repository.save(entry);
    }

    @EventHandler
    public void handleConfirmed(PaidFeeConfirmedEvent event) {
        final PaidFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setConfirmedDate(event.getConfirmedDate());
        entry.setFeeStatus(FeeStatus.CONFIRMED);
        entry.setSequenceNumber(event.getSequenceNumber());
        repository.save(entry);
    }

    @EventHandler
    public void handleOffseted(PaidFeeOffsetedEvent event) {
        final PaidFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setOffsetDate(event.getOffsetedDate());
        entry.setOffseted(true);
        entry.setOffsetId(event.getOffsetId().toString());
        repository.save(entry);
    }

    @EventHandler
    public void handleCancelled(PaidFeeCancelledEvent event) {
        final PaidFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setCancelledDate(event.getCancelledDate());
        entry.setFeeStatus(FeeStatus.CANCELLED);
        entry.setCancelledReason(event.getCancelledReason());
        repository.save(entry);
    }

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void setRepository(PaidFeeEntryQueryRepository repository) {
        this.repository = repository;
    }
}
