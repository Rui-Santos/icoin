package com.icoin.trading.fee.query.executed;

import com.icoin.trading.api.fee.domain.CommissionType;
import com.icoin.trading.api.fee.events.commission.BuyExecutedCommissionTransactionStartedEvent;
import com.icoin.trading.api.fee.events.commission.SellExecutedCommissionTransactionStartedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/17/14
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutedCommissionListener {
    private final static Logger logger = LoggerFactory.getLogger(ExecutedCommissionListener.class);
    private ExecutedCommissionEntryQueryRepository repository;


    @EventHandler
    public void handleSellCommission(SellExecutedCommissionTransactionStartedEvent event) {
        final ExecutedCommissionEntry entry = new ExecutedCommissionEntry();

        entry.setTradeType(event.getTradeType());
        entry.setCoinId(event.getCoinId().toString());
        entry.setCommissionAmount(event.getCommissionAmount());
        entry.setDueDate(event.getDueDate());
        entry.setExecutedMoney(event.getExecutedMoney());
        entry.setOrderBookId(event.getOrderBookId().toString());
        entry.setOrderId(event.getOrderId());
        entry.setPortfolioId(event.getPortfolioId().toString());
        entry.setTradeAmount(event.getTradeAmount());
        entry.setTradeTime(event.getTradeTime());
        entry.setType(CommissionType.SELL);
        entry.setOrderTransactionId(event.getOrderTransactionId().toString());
        entry.setTradedPrice(event.getTradedPrice());
        entry.setPrimaryKey(event.getFeeTransactionId().toString());


        repository.save(entry);
    }

    @EventHandler
    public void handleBuyCommission(BuyExecutedCommissionTransactionStartedEvent event) {
        final ExecutedCommissionEntry entry = new ExecutedCommissionEntry();

        entry.setTradeType(event.getTradeType());
        entry.setCoinId(event.getCoinId().toString());
        entry.setCommissionAmount(event.getCommissionAmount());
        entry.setDueDate(event.getDueDate());
        entry.setExecutedMoney(event.getExecutedMoney());
        entry.setOrderBookId(event.getOrderBookId().toString());
        entry.setOrderId(event.getOrderId());
        entry.setPortfolioId(event.getPortfolioId().toString());
        entry.setTradeAmount(event.getTradeAmount());
        entry.setTradeTime(event.getTradeTime());
        entry.setType(CommissionType.BUY);
        entry.setOrderTransactionId(event.getOrderTransactionId().toString());
        entry.setTradedPrice(event.getTradedPrice());
        entry.setPrimaryKey(event.getFeeTransactionId().toString());


        repository.save(entry);
    }

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public void setRepository(ExecutedCommissionEntryQueryRepository repository) {
        this.repository = repository;
    }
}
