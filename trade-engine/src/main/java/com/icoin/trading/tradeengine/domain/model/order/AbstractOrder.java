package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
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
    public static final BigDecimal SCALE = BigDecimal.valueOf(100000000);
    private TransactionId transactionId;
    private long itemPrice;
    private long tradeAmount;
    private PortfolioId portfolioId;
    private long itemRemaining;
    private Date placeDate;
    private CurrencyPair currencyPair;
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
        return BigDecimal.valueOf(itemPrice).divide(SCALE);
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice.multiply(SCALE).longValue();
    }

    public BigDecimal getTradeAmount() {
       return BigDecimal.valueOf(tradeAmount).divide(SCALE);
    }

    public void setTradeAmount(BigDecimal tradeAmount) {
        this.tradeAmount = tradeAmount.multiply(SCALE).longValue();
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(PortfolioId portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigDecimal getItemRemaining() {
        return BigDecimal.valueOf(itemRemaining).divide(SCALE);
    }

    public void setItemRemaining(BigDecimal itemRemaining) {
        this.itemRemaining = itemRemaining.multiply(SCALE).longValue();
    }

    public Date getPlaceDate() {
        return placeDate;
    }

    public void setPlaceDate(Date placeDate) {
        this.placeDate = placeDate;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setLastTradedTime(Date lastTradedTime) {
        this.lastTradedTime = lastTradedTime;
    }



    public Date getLastTradedTime() {
        return lastTradedTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    private void completeOrder(Date completeDate) {
        this.completeDate = completeDate == null ? currentTime() : completeDate;
        this.orderStatus = OrderStatus.DONE;
    }

    public void recordTraded(BigDecimal tradeAmount, Date lastTradedTime) {
        this.itemRemaining = itemRemaining - tradeAmount.multiply(SCALE).longValue();
        this.lastTradedTime = lastTradedTime;

        if (itemRemaining <= 0) {
            completeOrder(lastTradedTime);
        }
    }
}
