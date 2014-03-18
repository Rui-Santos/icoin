package com.icoin.trading.fee.query.fee;

import com.icoin.trading.api.fee.events.fee.AccountReceivableFeeCreatedEvent;
import com.icoin.trading.fee.query.fee.repositories.AccountReceivableFeeEntryQueryRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: PM9:22
 * To change this template use File | Settings | File Templates.
 */
public class AccountReceivableEntryListener {
    private AccountReceivableFeeEntryQueryRepository repository;

    @EventHandler
    public void handleSellOrderPlaced(AccountReceivableFeeCreatedEvent event) {
        AccountReceivableFeeEntry entry = new AccountReceivableFeeEntry();
        FeeEntry.create(event, entry);
        repository.save(entry);
    }



    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void setRepository(AccountReceivableFeeEntryQueryRepository repository) {
        this.repository = repository;
    }
}
