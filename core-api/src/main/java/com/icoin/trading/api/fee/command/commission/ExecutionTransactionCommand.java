package com.icoin.trading.api.fee.command.commission;

import com.homhon.base.command.CommandSupport;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.fee.domain.FeeTransactionId;
import com.icoin.trading.api.fee.domain.offset.OffsetId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.money.BigMoney;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 3/18/14
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionTransactionCommand<T extends ExecutionTransactionCommand> extends CommandSupport<T> {
    @NotNull
    protected final FeeTransactionId feeTransactionId;
    @NotNull
    protected final OffsetId offsetId;
    @NotNull
    protected final BigMoney commissionAmount;
    @NotEmpty
    protected final String orderId;
    @NotNull
    protected final TransactionId orderTransactionId;
    @NotNull
    protected final PortfolioId portfolioId;
    @NotNull
    protected final Date tradeTime;
    @NotNull
    protected final Date dueDate;
    @NotNull
    protected final TradeType tradeType;
    @NotNull
    protected final BigMoney tradedPrice;
    @NotNull
    protected final BigMoney tradeAmount;
    @NotNull
    protected final BigMoney executedMoney;
    @NotNull
    protected final OrderBookId orderBookId;
    @NotNull
    protected final CoinId coinId;

    public ExecutionTransactionCommand(FeeTransactionId feeTransactionId,
                                       OffsetId offsetId,
                                       BigMoney commissionAmount,
                                       String orderId,
                                       TransactionId orderTransactionId,
                                       PortfolioId portfolioId,
                                       Date tradeTime,
                                       Date dueDate,
                                       TradeType tradeType,
                                       BigMoney tradedPrice,
                                       BigMoney tradeAmount,
                                       BigMoney executedMoney,
                                       OrderBookId orderBookId,
                                       CoinId coinId) {
        this.feeTransactionId = feeTransactionId;
        this.offsetId = offsetId;
        this.commissionAmount = commissionAmount;
        this.orderId = orderId;
        this.orderTransactionId = orderTransactionId;
        this.portfolioId = portfolioId;
        this.tradeTime = tradeTime;
        this.dueDate = dueDate;
        this.tradeType = tradeType;
        this.tradedPrice = tradedPrice;
        this.tradeAmount = tradeAmount;
        this.executedMoney = executedMoney;
        this.orderBookId = orderBookId;
        this.coinId = coinId;
    }


    public FeeTransactionId getFeeTransactionId() {
        return feeTransactionId;
    }

    public BigMoney getCommissionAmount() {
        return commissionAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public TransactionId getOrderTransactionId() {
        return orderTransactionId;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public OffsetId getOffsetId() {
        return offsetId;
    }
}