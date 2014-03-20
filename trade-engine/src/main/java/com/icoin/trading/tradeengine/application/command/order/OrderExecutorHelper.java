package com.icoin.trading.tradeengine.application.command.order;

import com.icoin.trading.tradeengine.Constants;
import com.icoin.trading.api.coin.domain.CurrencyPair;
import com.icoin.trading.tradeengine.domain.model.commission.Commission;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicy;
import com.icoin.trading.tradeengine.domain.model.commission.CommissionPolicyFactory;
import com.icoin.trading.tradeengine.domain.model.order.Order;
import com.icoin.trading.tradeengine.domain.model.order.OrderBook;
import com.icoin.trading.api.tradeengine.domain.OrderBookId;
import com.icoin.trading.api.tradeengine.domain.OrderId;
import com.icoin.trading.tradeengine.domain.model.order.OrderRepository;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;

/**
 * Created with IntelliJ IDEA.
 * User: liougehooa
 * Date: 13-12-8
 * Time: PM10:10
 * Execute helper for order execution.
 * todo, if have time, change it to singleton with repo's set
 */
@Component
public class OrderExecutorHelper {
    private OrderRepository orderRepository;
    //    private BuyOrderRepository buyOrderRepository;
    private CommissionPolicyFactory commissionPolicyFactory;

    private static Logger logger = LoggerFactory.getLogger(OrderExecutorHelper.class);

    public List<Order> findAscPendingOrdersByPriceTime(Date toTime,
                                                       BigMoney price,
                                                       OrderBookId orderBookId,
                                                       int size) {
        notNull(toTime);
        notNull(price);
        isTrue(price.isPositive(), "Price should be greater than 0!");
        isTrue(size > 0, "Size should be greater than 0!");


        if (logger.isDebugEnabled()) {
            logger.debug("To find asc sell pending orders with toTime{}, price {}, orderBookId {}, size {} ",
                    toTime, price, orderBookId, size);
        }
        List<Order> list = orderRepository.findPendingSellOrdersByPriceTime(toTime, price, orderBookId, size);

        if (logger.isDebugEnabled()) {
            logger.debug("Found asc sell pending orders with toTime{}, price {}, orderBookId {}, size {}: ",
                    toTime, price, orderBookId, size, list);
        }
        return list;
    }


    public List<Order> findDescPendingOrdersByPriceTime(Date toTime,
                                                        BigMoney price,
                                                        OrderBookId orderBookId,
                                                        int size) {
        notNull(toTime);
        notNull(price);
        isTrue(price.isPositive(), "Price should be greater than 0!");
        isTrue(size > 0, "Size should be greater than 0!");


        if (logger.isDebugEnabled()) {
            logger.debug("To find desc buy pending orders with toTime {}, price {}, orderBookId {}, size {} ",
                    toTime, price, orderBookId, size);
        }

        List<Order> list = orderRepository.findPendingBuyOrdersByPriceTime(toTime, price, orderBookId, size);

        if (logger.isDebugEnabled()) {
            logger.debug("Found desc buy pending orders with toTime{}, price {}, orderBookId {}, size {}: ",
                    toTime, price, orderBookId, size, list);
        }
        return list;
    }

    public Order findSellOrder(OrderId orderId) {
        notNull(orderId);
        final Order sellOrder = orderRepository.findOne(orderId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("sellOrder {} found: {}", orderId, sellOrder);
        }

        return sellOrder;
    }

    public Order findBuyOrder(OrderId orderId) {
        notNull(orderId);
        final Order buyOrder = orderRepository.findOne(orderId.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("buyOrder {} found: {}", orderId, buyOrder);
        }

        return buyOrder;
    }

    public void refresh(OrderBook orderBook) {
        notNull(orderBook);
        notNull(orderBook.getCurrencyPair());
        notNull(orderBook.getOrderBookId());

        String lowestSellOrderId = null;
        String highestBuyOrderId = null;

        OrderBookId orderBookId = orderBook.getOrderBookId();
        CurrencyPair currencyPair = orderBook.getCurrencyPair();
        BigMoney lowestSellPrice = BigMoney.of(CurrencyUnit.of(currencyPair.getCounterCurrency()), Constants.INIT_SELL_PRICE);
        BigMoney highestBuyPrice = BigMoney.zero(CurrencyUnit.of(currencyPair.getCounterCurrency()));


        final Order lowestSell = orderRepository.findLowestPricePendingSellOrder(orderBookId);
        final Order highestBuy = orderRepository.findHighestPricePendingBuyOrder(orderBookId);

        if (lowestSell != null) {
            lowestSellOrderId = lowestSell.getPrimaryKey();
            lowestSellPrice = lowestSell.getItemPrice();
        }
        if (highestBuy != null) {
            highestBuyOrderId = highestBuy.getPrimaryKey();
            highestBuyPrice = highestBuy.getItemPrice();
        }

        logger.info("Refreshing with lowest sell order {} and price {}, Refreshing with highest buy order {} and price {}",
                lowestSellOrderId, lowestSellPrice, highestBuyOrderId, highestBuyPrice);
        orderBook.resetLowestSellPrice(lowestSellOrderId, lowestSellPrice);
        orderBook.resetHighestBuyPrice(highestBuyOrderId, highestBuyPrice);
    }

    public void recordTraded(Order buyOrder,
                             Order sellOrder,
                             BigMoney buyCommission,
                             BigMoney sellCommission,
                             BigMoney matchedTradeAmount,
                             BigMoney matchedTradePrice,
                             Date tradedDate) {
        notNull(buyOrder);
        notNull(sellOrder);


        buyOrder.recordTraded(matchedTradeAmount, buyCommission, tradedDate);
        sellOrder.recordTraded(matchedTradeAmount, sellCommission, tradedDate);
        logger.info("for this trade, buy order {} and sell order {} have commission {} and {}.",
                buyOrder, sellOrder, buyCommission, sellCommission);

        orderRepository.save(buyOrder);
        orderRepository.save(sellOrder);
    }

    public BigMoney calcExecutedSellCommission(Order sellOrder, BigMoney matchedTradePrice, BigMoney matchedTradeAmount) {
        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(sellOrder);
        Commission commission = commissionPolicy.calculateSellCommission(sellOrder, matchedTradeAmount, matchedTradePrice);
        return commission.getBigMoneyCommission();
    }

    public BigMoney calcExecutedBuyCommission(Order buyOrder, BigMoney matchedTradePrice, BigMoney matchedTradeAmount) {
        CommissionPolicy commissionPolicy = commissionPolicyFactory.createCommissionPolicy(buyOrder);
        Commission commission = commissionPolicy.calculateBuyCommission(buyOrder, matchedTradeAmount, matchedTradePrice);
        return commission.getBigMoneyCommission();
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Autowired
    public void setCommissionPolicyFactory(CommissionPolicyFactory commissionPolicyFactory) {
        this.commissionPolicyFactory = commissionPolicyFactory;
    }
}
