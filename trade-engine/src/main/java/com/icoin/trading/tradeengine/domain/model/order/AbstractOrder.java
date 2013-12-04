package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinExchangePair;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;

import java.math.BigDecimal;
import java.util.Date;

import static com.homhon.mongo.TimeUtils.currentTime;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM10:42
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-2
 * Time: PM10:42
 * To change this template use File | Settings | File Templates.
 */
public class AbstractOrder<T extends AbstractOrder> extends VersionedEntitySupport<T, String, Long> {
    private TransactionId transactionId;
    private BigDecimal itemPrice;
    private BigDecimal tradeAmount;
    private PortfolioId portfolioId;
    private BigDecimal itemsRemaining;
    private Date placeDate;
    private CoinExchangePair coinExchangePair;
    private final OrderType orderType;
    private OrderBookId orderBookId;
    private Date completeDate;
    private Date lastTradedTime;
    private OrderStatus orderStatus = OrderStatus.PENDING;


    public OrderBookId getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(OrderBookId orderBookId) {
        this.orderBookId = orderBookId;
    }


    public AbstractOrder(OrderType orderType) {
        notNull(orderType);
        this.orderType = orderType;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(TransactionId transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(PortfolioId portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigDecimal getItemsRemaining() {
        return itemsRemaining;
    }

    public void setItemsRemaining(BigDecimal itemsRemaining) {
        this.itemsRemaining = itemsRemaining;
    }

    public Date getPlaceDate() {
        return placeDate;
    }

    public void setPlaceDate(Date placeDate) {
        this.placeDate = placeDate;
    }

    public CoinExchangePair getCoinExchangePair() {
        return coinExchangePair;
    }

    public void setCoinExchangePair(CoinExchangePair coinExchangePair) {
        this.coinExchangePair = coinExchangePair;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    private void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public Date getLastTradedTime() {
        return lastTradedTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    private void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    private void completeOrder(Date completeDate) {
        this.completeDate = completeDate == null ? currentTime() : completeDate;
        this.orderStatus = OrderStatus.DONE;
    }

    public void recordTraded(BigDecimal tradeAmount, Date lastTradedTime) {
        this.itemsRemaining = itemsRemaining.subtract(tradeAmount);
        this.lastTradedTime = lastTradedTime;

        if (BigDecimal.ZERO.compareTo(itemsRemaining) >= 0) {
            completeOrder(lastTradedTime);
        }
    }
}
