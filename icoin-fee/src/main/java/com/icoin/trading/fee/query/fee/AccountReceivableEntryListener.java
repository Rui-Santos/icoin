package com.icoin.trading.fee.query.fee;

import com.icoin.trading.api.fee.domain.fee.FeeStatus;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeCancelledEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeConfirmedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeCreatedEvent;
import com.icoin.trading.api.fee.events.fee.receivable.AccountReceivableFeeOffsetedEvent;
import com.icoin.trading.fee.query.fee.repositories.AccountReceivableFeeEntryQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:22
 * To change this template use File | Settings | File Templates.
 */
public class AccountReceivableEntryListener {
    private static Logger logger = LoggerFactory.getLogger(AccountReceivableEntryListener.class);
    private AccountReceivableFeeEntryQueryRepository repository;

    @EventHandler
    public void handleSellOrderPlaced(AccountReceivableFeeCreatedEvent event) {
        AccountReceivableFeeEntry entry = new AccountReceivableFeeEntry();
        entry.copy(event);
        repository.save(entry);
    }

    @EventHandler
    public void handleConfirmed(AccountReceivableFeeConfirmedEvent event) {
        final AccountReceivableFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setConfirmedDate(event.getConfirmedDate());
        entry.setFeeStatus(FeeStatus.CONFIRMED);
        repository.save(entry);
    }

    @EventHandler
    public void handleOffseted(AccountReceivableFeeOffsetedEvent event) {
        final AccountReceivableFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setOffsetDate(event.getOffsetedDate());
        entry.setOffseted(true);
        repository.save(entry);
    }

    @EventHandler
    public void handleCancelled(AccountReceivableFeeCancelledEvent event) {
        final AccountReceivableFeeEntry entry = repository.findOne(event.getFeeId().toString());

        if (entry == null) {
            logger.error("Entry not found with id {}", event.getFeeId());
        }

        entry.setCancelledDate(event.getCancelledDate());
        entry.setFeeStatus(FeeStatus.CANCELLED);
        repository.save(entry);
    }

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void setRepository(AccountReceivableFeeEntryQueryRepository repository) {
        this.repository = repository;
    }
}
