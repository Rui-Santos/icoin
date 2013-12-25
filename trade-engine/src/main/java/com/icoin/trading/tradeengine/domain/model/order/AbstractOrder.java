package com.icoin.trading.tradeengine.domain.model.order;

import com.homhon.mongo.domainsupport.modelsupport.entity.VersionedEntitySupport;
import com.icoin.trading.tradeengine.domain.model.coin.CoinId;
import com.icoin.trading.tradeengine.domain.model.coin.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.portfolio.PortfolioId;
import com.icoin.trading.tradeengine.domain.model.transaction.TransactionId;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

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
    private BigMoney itemPrice;
    private BigMoney tradeAmount;
    private PortfolioId portfolioId;
    private BigMoney itemRemaining;
    private BigMoney totalCommission;
    private Date placeDate;
    private CurrencyPair currencyPair;
    private CoinId coinId;
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

    public CoinId getCoinId() {
        return coinId;
    }

    public void setCoinId(CoinId coinId) {
        this.coinId = coinId;
    }

    public BigMoney getItemPrice() {
        return itemPrice;
//        return BigDecimal.valueOf(itemPrice).divide(SCALE);
    }

    public void setItemPrice(BigMoney itemPrice) {
        this.itemPrice = itemPrice;
//        this.itemPrice = itemPrice.multiply(SCALE).longValue();
    }

    public BigMoney getTradeAmount() {
        return tradeAmount;
//       return BigDecimal.valueOf(tradeAmount).divide(SCALE);
    }

    public void setTradeAmount(BigMoney tradeAmount) {
        this.tradeAmount = tradeAmount;
//        this.tradeAmount = tradeAmount.multiply(SCALE).longValue();
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(PortfolioId portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigMoney getItemRemaining() {
        return itemRemaining;
//        return BigDecimal.valueOf(itemRemaining).divide(SCALE);
    }

    public void setItemRemaining(BigMoney itemRemaining) {
        this.itemRemaining = itemRemaining;
//        this.itemRemaining = itemRemaining.multiply(SCALE).longValue();
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

    public BigMoney getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigMoney totalCommission) {
        this.totalCommission = totalCommission;
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
        this.itemRemaining =
                itemRemaining.minus(tradeAmount);
        if (commission == null) {
            totalCommission = BigMoney.zero(commission.getCurrencyUnit());
        }

        totalCommission = totalCommission.plus(commission);

//                itemRemaining.minus(Money.of(CurrencyUnit.getInstance(Currencies.BTC), tradeAmount)) - tradeAmount.multiply(SCALE).longValue();
        this.lastTradedTime = lastTradedTime;

        if (itemRemaining.isNegativeOrZero()) {
            completeOrder(lastTradedTime);
        }
    }
}
