package com.icoin.trading.api.tradeengine.command.portfolio.cash;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-14
 * Time: AM11:39
 * To change this template use File | Settings | File Templates.
 */
public class ClearReservedCashCommand extends CommandSupport<ClearReservedCashCommand> {
    private PortfolioId portfolioIdentifier;
    private TransactionId transactionIdentifier;
    private OrderBookId orderBookIdentifier;
    private BigMoney leftReservedMoney;
    private BigMoney leftCommission;
    @NotNull
    private Date time;

    public ClearReservedCashCommand(PortfolioId portfolioIdentifier,
                                    TransactionId transactionIdentifier,
                                    OrderBookId orderBookIdentifier,
                                    BigMoney leftReservedMoney,
                                    BigMoney leftCommission,
                                    Date time) {
        this.portfolioIdentifier = portfolioIdentifier;
        this.transactionIdentifier = transactionIdentifier;
        this.orderBookIdentifier = orderBookIdentifier;
        this.leftReservedMoney = leftReservedMoney;
        this.leftCommission = leftCommission;
        this.time = time;
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

    public BigMoney getLeftCommission() {
        return leftCommission;
    }

    public Date getTime() {
        return time;
    }
}
