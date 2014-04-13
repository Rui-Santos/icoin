package com.icoin.trading.fee.query.fee.payable;

import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.payable.AccountPayableFeeOffsetedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-4-13
 * Time: PM5:27
 * To change this template use File | Settings | File Templates.
 */
public class AccountPayableEntryListener {
    private static Logger logger = LoggerFactory.getLogger(AccountPayableEntryListener.class);
    private AccountPayableFeeEntryQueryRepository repository;

    @EventHandler
    public void handleCreated(AccountPayableFeeCreatedEvent event) {
        AccountPayableFeeEntry entry = new AccountPayableFeeEntry();
        entry.copy(event);
        repository.save(entry);
    }

    @EventHandler
    public void handleConfirmed(AccountPayableFeeConfirmedEvent event) {
        final AccountPayableFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setConfirmedDate(event.getConfirmedDate());
        entry.setFeeStatus(FeeStatus.CONFIRMED);
        repository.save(entry);
    }

    @EventHandler
    public void handleOffseted(AccountPayableFeeOffsetedEvent event) {
        final AccountPayableFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setOffsetDate(event.getOffsetedDate());
        entry.setOffseted(true);
        entry.setOffsetId(event.getOffsetId().toString());
        repository.save(entry);
    }

    @EventHandler
    public void handleCancelled(AccountPayableFeeCancelledEvent event) {
        final AccountPayableFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setCancelledDate(event.getCancelledDate());
        entry.setCancelledReason(event.getCancelledReason());
        entry.setFeeStatus(FeeStatus.CANCELLED);
        repository.save(entry);
    }

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void setRepository(AccountPayableFeeEntryQueryRepository repository) {
        this.repository = repository;
    }
}