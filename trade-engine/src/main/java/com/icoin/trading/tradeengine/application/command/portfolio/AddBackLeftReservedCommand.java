package com.icoin.trading.tradeengine.application.command.portfolio;

import com.icoin.trading.tradeengine.domain.model.order.OrderBookId;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-14
 * Time: AM11:39
 * To change this template use File | Settings | File Templates.
 */
public class AddBackLeftReservedCommand {
    private PortfolioId portfolioIdentifier;
    private TransactionId transactionIdentifier;
    private OrderBookId orderBookIdentifier;
    private BigMoney leftReservedMoney;

    public AddBackLeftReservedCommand(PortfolioId portfolioIdentifier,
                                      TransactionId transactionIdentifier,
                                      OrderBookId orderBookIdentifier,
                                      BigMoney leftReservedMoney) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.transactionIdentifier = transactionIdentifier;
        this.orderBookIdentifier = orderBookIdentifier;
        this.leftReservedMoney = leftReservedMoney;
    }

    public PortfolioId getPortfolioIdentifier() {
        return portfolioIdentifier;
    }

    public TransactionId getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public OrderBookId getOrderBookIdentifier() {
        return orderBookIdentifier;
    }

    public BigMoney getLeftReservedMoney() {
        return leftReservedMoney;
    }
}
