package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.PortfolioId;
import com.icoin.trading.api.tradeengine.domain.TransactionId;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import java.util.Date;

import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.TimeUtils.currentTime;

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
public class Order<T extends Order> extends VersionedEntitySupport<T, String, Long> {
    private TransactionId transactionId;
    private BigMoney itemPrice;
    private BigMoney tradeAmount;
    private PortfolioId portfolioId;
    private BigMoney itemRemaining;
    private BigMoney leftCommission;
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

    public Order(OrderType orderType) {
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

    public BigMoney getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigMoney itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(BigMoney tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(PortfolioId portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigMoney getItemRemaining() {
        return itemRemaining;
    }

    public void setItemRemaining(BigMoney itemRemaining) {
        this.itemRemaining = itemRemaining;
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

    public CurrencyUnit getBaseCurrency() {
        return currencyPair.getBaseCurrencyUnit();
    }


    public CurrencyUnit getCounterCurrency() {
        return currencyPair.getCounterCurrencyUnit();
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

    public BigMoney getLeftCommission() {
        return leftCommission;
    }

    public void setLeftCommission(BigMoney leftCommission) {
        this.leftCommission = leftCommission;
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

    public void recordTraded(BigMoney tradeAmount, BigMoney commission, Date lastTradedTime) {
        this.itemRemaining = itemRemaining.minus(tradeAmount);

        leftCommission = leftCommission.minus(commission);

        this.lastTradedTime = lastTradedTime;

        if (itemRemaining.isNegativeOrZero()) {
            completeOrder(lastTradedTime);
        }
    }
}
