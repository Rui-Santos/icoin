package com.icoin.trading.api.fee.events.commission;

import com.homhon.base.domain.event.EventSupport;
import com.icoin.trading.api.coin.domain.CoinId;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TradeType;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 14-3-18
 * Time: AM9:14
 * To change this template use File | Settings | File Templates.
 */
public class CommissionStartedEvent <T extends CommissionStartedEvent> extends EventSupport<T> {
    private final BigMoney tradeAmount;
    private final BigMoney tradedPrice;
    private final String buyOrderId;
    private final CoinId coinId;
    private final String sellOrderId;
    private final TransactionId buyTransactionId;
    private final TransactionId sellTransactionId;
    private final OrderBookId orderBookId;
    private final Date tradeTime;
    private final TradeType tradeType;
    private final BigMoney buyCommission;
    private final BigMoney sellCommission;
    private final BigMoney executedMoney;
    private final PortfolioId buyPortfolioId;
    private final PortfolioId sellPortfolioId;

    public CommissionStartedEvent(BigMoney tradeAmount,
                                  BigMoney tradedPrice,
                                  String buyOrderId,
                                  CoinId coinId,
                                  String sellOrderId,
                                  TransactionId buyTransactionId,
                                  TransactionId sellTransactionId,
                                  OrderBookId orderBookId,
                                  Date tradeTime,
                                  TradeType tradeType,
                                  BigMoney buyCommission,
                                  BigMoney sellCommission,
                                  BigMoney executedMoney,
                                  PortfolioId buyPortfolioId,
                                  PortfolioId sellPortfolioId) {
        this.tradeAmount = tradeAmount;
        this.tradedPrice = tradedPrice;
        this.buyOrderId = buyOrderId;
        this.coinId = coinId;
        this.sellOrderId = sellOrderId;
        this.buyTransactionId = buyTransactionId;
        this.sellTransactionId = sellTransactionId;
        this.orderBookId = orderBookId;
        this.tradeTime = tradeTime;
        this.tradeType = tradeType;
        this.buyCommission = buyCommission;
        this.sellCommission = sellCommission;
        this.executedMoney = executedMoney;
        this.buyPortfolioId = buyPortfolioId;
        this.sellPortfolioId = sellPortfolioId;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public BigMoney getTradedPrice() {
        return tradedPrice;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public CoinId getCoinId() {
        return coinId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public TransactionId getBuyTransactionId() {
        return buyTransactionId;
    }

    public TransactionId getSellTransactionId() {
        return sellTransactionId;
    }

    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BigMoney getBuyCommission() {
        return buyCommission;
    }

    public BigMoney getSellCommission() {
        return sellCommission;
    }

    public BigMoney getExecutedMoney() {
        return executedMoney;
    }

    public PortfolioId getBuyPortfolioId() {
        return buyPortfolioId;
    }

    public PortfolioId getSellPortfolioId() {
        return sellPortfolioId;
    }
}
